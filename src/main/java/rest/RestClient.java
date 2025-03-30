package rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.function.FailablePredicate;
import org.apache.http.params.CoreConnectionPNames;
import org.brotli.dec.BrotliInputStream;
import org.slf4j.Logger;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.net.InternetDomainName;

import botrix.commons.http.URIUtils;
import botrix.internal.logging.LoggerFactory;
import botrix.internal.prefs.Preferences;
import botrix.utils.WaitUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopRuntimeException;

/**
 * REST Client built for easy to use wrappers for REST Service interactions
 * 
 * @author Mayur Prajapati
 *
 */
// TODO: implement timeout https://www.baeldung.com/httpclient-timeout
@ExtensionMethod({ WaitUtils.class })
public class RestClient {
	static {
		G.setup();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
	private RestClientBuilder builder;
	private Map<String, String> existingCookies = Collections.synchronizedMap(new HashMap<>());
	private Preferences prefs = Preferences.user(RestClient.class.getName());

	private RestClient(RestClientBuilder builder) {
		// UnknownHostException

		this.builder = builder;

		RequestSpecBuilder specBuilder = Objects.requireNonNullElseGet(builder.getRequestSpecBuilder(),
				RequestSpecBuilder::new);

		RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()//
				.setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, builder.getConnectionTimeout())//
				.setParam(CoreConnectionPNames.SO_TIMEOUT, builder.getSoTimeout()));
		specBuilder.setConfig(config);

		cookiesFilter(specBuilder);

		retryFilter(specBuilder);

		builder.setRequestSpecBuilder(specBuilder);
	}

	public static String decodeCompressedResponse(Response r) throws Exception {
		String encoding = r.getHeader("Content-Encoding");
		String responseBody;
		if ("gzip".equalsIgnoreCase(encoding)) {
			// Decompress GZIP
			try (GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(r.asByteArray()));
					InputStreamReader isr = new InputStreamReader(gzipStream, "UTF-8");
					BufferedReader reader = new BufferedReader(isr)) {

				StringBuilder decompressedResponse = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					decompressedResponse.append(line);
				}
				responseBody = decompressedResponse.toString();
			}
		} else if ("deflate".equalsIgnoreCase(encoding)) {
			// Decompress Deflate
			try (InflaterInputStream deflateStream = new InflaterInputStream(new ByteArrayInputStream(r.asByteArray()));
					InputStreamReader isr = new InputStreamReader(deflateStream, "UTF-8");
					BufferedReader reader = new BufferedReader(isr)) {

				StringBuilder decompressedResponse = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					decompressedResponse.append(line);
				}
				responseBody = decompressedResponse.toString();
			}
		} else if ("br".equalsIgnoreCase(encoding)) {
			// Decompress Brotli
			try (BrotliInputStream brotliStream = new BrotliInputStream(new ByteArrayInputStream(r.asByteArray()));
					InputStreamReader isr = new InputStreamReader(brotliStream, "UTF-8");
					BufferedReader reader = new BufferedReader(isr)) {

				StringBuilder decompressedResponse = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					decompressedResponse.append(line);
				}
				responseBody = decompressedResponse.toString();
			} catch (Exception e) {
				throw new RuntimeException("Failed to decompress Brotli response", e);
			}
		} else {
			// Assume uncompressed
			responseBody = r.getBody().asString();
		}
		return responseBody;
	}

	private void retryFilter(RequestSpecBuilder specBuilder) {
		specBuilder.addFilter((req, res, context) -> {
			Response r = null;
			for (int i = 0; i < builder.errorRetryCount; i++) {
				try {
					r = context.next(req, res);
					return Validate.notNull(r, "Not able to reach the server");
				} catch (Throwable e) {
					if (i == builder.errorRetryCount - 1) {
						throw e;
					}

					LOGGER.debug("Retrying request again", e);
					Duration.ofMillis(500).sleep();
				}
			}

			return r;
		});
	}

	public Map<String, String> getCurrentCookies() {
		return existingCookies;
	}

	private void cookiesFilter(RequestSpecBuilder specBuilder) {
		if (!builder.isManageCookies()) {
			return;
		}

		specBuilder.addFilter((req, res, context) -> {
			String host = URIUtils.getUri(req.getURI()).getHost();
			InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
			String domain = internetDomainName.toString();

//			Map<String, String> existingCookies = new HashMap<>(getCookies(domain));
			req = (FilterableRequestSpecification) req.cookies(existingCookies);

			Response r = context.next(req, res);

			Map<String, String> map = new HashMap<>();

			// merge both cookies
			map.putAll(existingCookies);
			map.putAll(r.cookies());

			// now that both cookies objects are merged we can clear this one and use merged
			// one
			existingCookies.clear();
			existingCookies.putAll(map);

//			existingCookies.putAll(r.detailedCookies());
//			setCookies(domain, existingCookies);
			return r;
		});
	}

	public static RestClientBuilder builder() {
		return new RestClientBuilder();
	}

	public RequestSpecification given() {
		/**
		 * wrapping "builder.getSpecBuilder().build()" with given() because it'll copy
		 * the spec & not modify base one.
		 */
		RequestSpecification spec = RestAssured.given(builder.getRequestSpecBuilder().build());

//		if (builder.isManageCookies()) {
//			var specCasted = (RequestSpecificationImpl) spec;
//
//			URI uri = URIUtils.getUri(specCasted.getURI());
//			Map<String, String> existingCookies = getCookies(uri.getHost());
//			spec = spec.cookies(existingCookies);
//		}

		return spec;
	}

	public static Response givenRetryUntilSuccessSilent(Function<Integer, Response> apiTrigger, int maxRetryCount) {
		return givenRetrySilent(apiTrigger, (r) -> HttpStatusCodes.isSuccess(r.statusCode()), maxRetryCount);
	}

	public static Response givenRetryUntilSuccessThrowing(Function<Integer, Response> apiTrigger, int maxRetryCount)
			throws Throwable {
		return givenRetryThrowing(apiTrigger, (r) -> HttpStatusCodes.isSuccess(r.statusCode()), maxRetryCount);
	}

	public static Response givenRetrySilent(Function<Integer, Response> apiTrigger, ResponseSpecification responseSpec,
			int maxRetryCount) {
		try {
			return givenRetryThrowing(apiTrigger, responseSpec, maxRetryCount);
		} catch (Throwable ignored) {
		}

		return null;
	}

	public static Response givenRetrySilent(Function<Integer, Response> apiTrigger,
			FailablePredicate<Response, Throwable> responseMatcher, int maxRetryCount) {
		try {
			return givenRetryThrowing(apiTrigger, responseMatcher, maxRetryCount);
		} catch (Throwable ignored) {
		}

		return null;
	}

	public static Response givenRetryThrowing(Function<Integer, Response> apiTrigger,
			ResponseSpecification responseSpec, int maxRetryCount) throws Throwable {
		return givenRetryThrowing(apiTrigger, (r) -> {
			r.then().spec(responseSpec).extract().response();
			return true;
		}, maxRetryCount);
	}

	public static Response givenRetryThrowing(Function<Integer, Response> apiTrigger,
			FailablePredicate<Response, Throwable> responseMatcher, int maxRetryCount) throws Throwable {
		Response r = null;
		int c = 0;
		Throwable t = null;

		int count = 0;
		do {
			try {
				r = apiTrigger.apply(count++);
				if (responseMatcher.test(r))
					return r;

				Thread.sleep(1000);
				throw new BishopRuntimeException("Unexpected response");
			} catch (Throwable e) {
				t = e;
			}
		} while ((++c) < maxRetryCount);

		throw t;
	}

	public static <T> List<T> multithreaded(List<Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return multithreaded(10, tasks);
	}

	public static <T> List<T> multithreaded(int threadCount, List<Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<Future<T>> futures = new ArrayList<>();

		futures = executor.invokeAll(tasks);

		executor.shutdownNow();

		List<T> responses = new ArrayList<>();
		for (Future<T> future : futures) {
			responses.add(future.get());
		}

		return responses;
	}

	public static void logResponseError(Response r) {
		if (r == null)
			return;
		LOGGER.error("Status Code: {}", r.statusCode());
	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class RestClientBuilder {
		private boolean manageCookies = false;
		private RequestSpecBuilder requestSpecBuilder;
		private int errorRetryCount = 1;
		private int connectionTimeout = 30_000;
		private int soTimeout = 30_000;

		public RestClient build() {
			return new RestClient(this);
		}
	}
}
