package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.Route;

import botrix.internal.logging.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Page {
	private static final Logger logger = LoggerFactory.getLogger(Page.class);
	private com.microsoft.playwright.Page page;

	public boolean hasUrl(String url) {
		return new HasURL(this, url).fulfill();
	}

	public boolean hasUrl(String url, Duration timeout) {
		return hasUrlOptions(url).timeout(timeout).fulfill();
	}

	public HasURL hasUrlOptions(String url) {
		return new HasURL(this, url);
	}

	public WaitForURL waitForUrlOptions(String url) {
		return new WaitForURL(this, url);
	}

	public void waitForURL(String url) {
		page.waitForURL(url);
	}
	
	public String url() {
		return page.url();
	}

	public void waitForURL(String url, Duration timeout) {
		new WaitForURL(this, url).timeout(timeout).fulfill();
	}

	public void waitForTimeout(Duration timeout) {
		page.waitForTimeout(timeout.toMillis());
	}

	public Locator locator(String selector, String name) {
		return new Locator(page.locator(selector), name);
	}

	public void navigate(String url) {
		page.navigate(url);
		logger.info("Navigated to {}", url);
	}

	public void reload() {
		page.reload();
		logger.info("Reload successful");
	}

	public FrameLocator frameLocator(String locator, String name) {
		return new FrameLocator(page.frameLocator(locator), name);
	}

	public void onDialog(Consumer<Dialog> consumer) {
		page.onDialog((d) -> {
			consumer.accept(new Dialog(d));
		});
	}

	public void onRequest(Consumer<RequestWrapper> handler) {
		page.onRequest((Request r) -> {
			handler.accept(new RequestWrapper(r));
		});
	}

	public void route(String url, Consumer<Route> route) {
		page.route(url, (r) -> {
			route.accept(r);
		});
	}

	public void route(Pattern url, Consumer<Route> route) {
		page.route(url, (r) -> {
			route.accept(r);
		});
	}

	public void route(Predicate<String> url, Consumer<Route> route) {
		page.route(url, (r) -> {
			route.accept(r);
		});
	}

	public APIRequestContext request() {
		return page.request();
	}

	public void onResponse(Consumer<ResponseWrapper> handler) {
		page.onResponse((Response r) -> {
			handler.accept(new ResponseWrapper(r));
		});
	}

	public Page waitForPopup(Runnable callback) {
		return waitForPopupOptions(callback).fulfill();
	}

	public WaitForPopupOptions waitForPopupOptions(Runnable callback) {
		return new WaitForPopupOptions(this, callback);
	}

	public ResponseWrapper waitForResponse(String globUrlPattern, Runnable callback) {
		return new ResponseWrapper(page.waitForResponse(globUrlPattern, callback));
	}

	public ResponseWrapper waitForResponse(Pattern regex, Runnable callback) {
		return new ResponseWrapper(page.waitForResponse(regex, callback));
	}

	public static enum ScrollDirection {
		Up, Down
	}

	public static enum ScrollSpeed {
		Slow, Fast
	}

	public void scrollToTopFast() {
		scroll(ScrollDirection.Up, ScrollSpeed.Fast);
	}

	public void scrollToTopSlow() {
		scroll(ScrollDirection.Up, ScrollSpeed.Slow);
	}

	public void scrollToDownFast() {
		scroll(ScrollDirection.Down, ScrollSpeed.Fast);
	}

	public void scrollToDownSlow() {
		scroll(ScrollDirection.Down, ScrollSpeed.Slow);
	}

	public void scroll(ScrollDirection direction, ScrollSpeed speed) {
		String js = """
				async (args) => {
				    const {direction, speed} = args;
				    const delay = ms => new Promise(resolve => setTimeout(resolve, ms));
				    const scrollHeight = () => window.pageYOffset;
				    const start = direction === "down" ? 0 : scrollHeight();
				    const shouldStop = (position) => direction === "down" ? position > scrollHeight() : position < 0;
				    const increment = direction === "down" ? 100 : -100;
				    const delayTime = speed === "slow" ? 50 : 10;
				    console.error(start, shouldStop(start), increment)
				    for (let i = start; !shouldStop(i); i += increment) {
				        window.scrollTo(0, i);
				        await delay(delayTime);
				    }
				}
				""";
		Map<String, String> args = Map.of("direction", direction.toString().toLowerCase(), "speed",
				speed.toString().toLowerCase());
		evaluateAs(js, args);
	}

	public ResponseWrapper waitForResponse(Predicate<ResponseWrapper> responsePredicate, Runnable callback) {
		return new ResponseWrapper(page.waitForResponse((r) -> {
			return responsePredicate.test(new ResponseWrapper(r));
		}, callback));
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluateAs(String expression) {
		return (T) evaluate(expression);
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluateAs(String expression, Object arg) {
		return (T) evaluate(expression, arg);
	}

	public Object evaluate(String expression) {
		return page.evaluate(expression);
	}

	public Object evaluate(String expression, Object arg) {
		return page.evaluate(expression, arg);
	}

	public KeyboardWrapper keyboard() {
		return new KeyboardWrapper(page.keyboard());
	}

	public MouseWrapper mouse() {
		return new MouseWrapper(page.mouse());
	}
}
