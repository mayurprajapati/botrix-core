package botrix.internal.playwright.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;

import botrix.internal.logging.LoggerFactory;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;

/**
 * A lightweight, AutoCloseable wrapper around Playwright's Chromium browser.
 * Designed for quick headless sessions like cookie extraction.
 *
 * <p>Usage:
 * <pre>
 * try (var browser = PlaywrightBrowser.chromium().headless(true).noSandbox(true).launch()) {
 *     browser.navigate("https://example.com");
 *     var cookies = browser.getCookiesForRestAssured();
 * }
 * </pre>
 */
public class PlaywrightBrowser implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightBrowser.class);

	private final Playwright playwright;
	private final Browser browser;
	private final BrowserContext context;
	private final com.microsoft.playwright.Page page;

	PlaywrightBrowser(Playwright playwright, Browser browser, BrowserContext context,
			com.microsoft.playwright.Page page) {
		this.playwright = playwright;
		this.browser = browser;
		this.context = context;
		this.page = page;
	}

	/**
	 * Start building a Chromium browser session.
	 */
	public static Builder chromium() {
		return new Builder();
	}

	/**
	 * Navigate to the given URL (waits for page load).
	 */
	public void navigate(String url) {
		page.navigate(url);
		LOGGER.info("Navigated to {}", url);
	}

	/**
	 * Get the wrapped Playwright page.
	 */
	public Page getPage() {
		return new Page(page);
	}

	/**
	 * Get all cookies from the browser context.
	 */
	public List<com.microsoft.playwright.options.Cookie> getCookies() {
		return context.cookies();
	}

	/**
	 * Get all cookies converted to RestAssured format.
	 */
	public Cookies getCookiesForRestAssured() {
		List<Cookie> restCookies = getCookies().stream()
				.map(c -> {
					Cookie.Builder b = new Cookie.Builder(c.name, c.value)
							.setDomain(c.domain)
							.setPath(c.path)
							.setSecured(c.secure)
							.setHttpOnly(c.httpOnly);
					if (c.expires != null && c.expires > 0) {
						b.setExpiryDate(new Date((long) (c.expires * 1000)));
					}
					return b.build();
				})
				.collect(Collectors.toList());
		return new Cookies(restCookies);
	}

	@Override
	public void close() {
		try {
			context.close();
		} catch (Exception e) {
			LOGGER.warn("Failed to close context: {}", e.getMessage());
		}
		try {
			browser.close();
		} catch (Exception e) {
			LOGGER.warn("Failed to close browser: {}", e.getMessage());
		}
		try {
			playwright.close();
		} catch (Exception e) {
			LOGGER.warn("Failed to close playwright: {}", e.getMessage());
		}
	}

	/**
	 * Builder for configuring and launching a PlaywrightBrowser.
	 */
	public static class Builder {
		private boolean headless = true;
		private boolean noSandbox = false;
		private String userAgent = null;
		private Double slowMo = null;
		private final List<String> extraArgs = new ArrayList<>();

		Builder() {
		}

		public Builder headless(boolean headless) {
			this.headless = headless;
			return this;
		}

		public Builder noSandbox(boolean noSandbox) {
			this.noSandbox = noSandbox;
			return this;
		}

		public Builder userAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public Builder slowMo(double slowMo) {
			this.slowMo = slowMo;
			return this;
		}

		public Builder addArg(String arg) {
			this.extraArgs.add(arg);
			return this;
		}

		public PlaywrightBrowser launch() {
			Playwright playwright = Playwright.create();

			List<String> args = new ArrayList<>();
			if (noSandbox) {
				args.add("--no-sandbox");
			}
			args.add("--disable-blink-features=AutomationControlled");
			args.add("--disable-infobars");
			args.addAll(extraArgs);

			LaunchOptions launchOptions = new LaunchOptions();
			launchOptions.setHeadless(headless);
			launchOptions.setArgs(args);
			if (slowMo != null) {
				launchOptions.setSlowMo(slowMo);
			}

			Browser browser = playwright.chromium().launch(launchOptions);

			NewContextOptions contextOptions = new NewContextOptions();
			if (userAgent != null) {
				contextOptions.setUserAgent(userAgent);
			}

			BrowserContext context = browser.newContext(contextOptions);
			com.microsoft.playwright.Page page = context.newPage();

			return new PlaywrightBrowser(playwright, browser, context, page);
		}
	}
}
