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
	 * Get the underlying BrowserContext.
	 */
	public BrowserContext getContext() {
		return context;
	}

	/**
	 * Get all cookies from the browser context.
	 */
	public List<com.microsoft.playwright.options.Cookie> getCookies() {
		return context.cookies();
	}

	/**
	 * Add cookies to the browser context from RestAssured Cookies.
	 * Infers the domain from the current page URL if cookies lack one.
	 */
	public void addCookies(Cookies restAssuredCookies) {
		// Try to infer domain from the current page URL
		String currentUrl = page.url();
		String inferredDomain = null;
		if (currentUrl != null && !currentUrl.isBlank() && !currentUrl.equals("about:blank")) {
			try {
				inferredDomain = new java.net.URI(currentUrl).getHost();
			} catch (Exception ignored) {
			}
		}
		addCookies(restAssuredCookies, inferredDomain);
	}

	/**
	 * Add cookies to the browser context from RestAssured Cookies,
	 * using the given defaultDomain for any cookie that lacks one.
	 */
	public void addCookies(Cookies restAssuredCookies, String defaultDomain) {
		List<com.microsoft.playwright.options.Cookie> pwCookies = new ArrayList<>();
		for (Cookie c : restAssuredCookies) {
			String domain = c.getDomain();
			if (domain == null || domain.isBlank()) {
				domain = defaultDomain;
			}
			if (domain == null || domain.isBlank()) {
				LOGGER.warn("Skipping cookie '{}' — no domain available", c.getName());
				continue;
			}
			// Playwright expects domain to start with a dot for subdomains
			if (!domain.startsWith(".")) {
				domain = "." + domain;
			}

			com.microsoft.playwright.options.Cookie pwCookie = new com.microsoft.playwright.options.Cookie(c.getName(), c.getValue());
			pwCookie.setDomain(domain);
			pwCookie.setPath(c.getPath() != null ? c.getPath() : "/");
			pwCookie.setSecure(c.isSecured());
			pwCookie.setHttpOnly(c.isHttpOnly());
			if (c.getExpiryDate() != null) {
				pwCookie.setExpires(c.getExpiryDate().getTime() / 1000.0);
			}
			pwCookies.add(pwCookie);
		}
		context.addCookies(pwCookies);
		LOGGER.info("Added {} cookies to browser context", pwCookies.size());
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
		if (browser != null) {
			try {
				browser.close();
			} catch (Exception e) {
				LOGGER.warn("Failed to close browser: {}", e.getMessage());
			}
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
		private String userDataDir = null;
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

		/**
		 * Set a user data directory for a persistent browser context.
		 * Cookies, localStorage, and session data will be saved/restored from this path.
		 */
		public Builder userDataDir(String userDataDir) {
			this.userDataDir = userDataDir;
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

			if (userDataDir != null) {
				// Persistent context mode — cookies & session persist on disk
				var persistentOptions = new com.microsoft.playwright.BrowserType.LaunchPersistentContextOptions();
				persistentOptions.setHeadless(headless);
				persistentOptions.setArgs(args);
				if (slowMo != null) {
					persistentOptions.setSlowMo(slowMo);
				}
				if (userAgent != null) {
					persistentOptions.setUserAgent(userAgent);
				}
				// Required for --start-maximized to work — no fixed viewport
				if (args.contains("--start-maximized")) {
					persistentOptions.setViewportSize(null);
				}

				java.nio.file.Path dataDir = java.nio.file.Paths.get(userDataDir);
				BrowserContext context = playwright.chromium().launchPersistentContext(dataDir, persistentOptions);
				com.microsoft.playwright.Page page = context.pages().isEmpty() ? context.newPage() : context.pages().get(0);

				LOGGER.info("Launched persistent context at {}", userDataDir);
				return new PlaywrightBrowser(playwright, null, context, page);
			}

			// Standard (ephemeral) mode
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
