package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.microsoft.playwright.TimeoutError;

import botrix.internal.logging.LoggerFactory;
import botrix.internal.playwright.Fulfillable;

public class HasURL implements Fulfillable<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(HasURL.class);

	private com.microsoft.playwright.Page.WaitForURLOptions pageWaitForUrlOptions = new com.microsoft.playwright.Page.WaitForURLOptions();
	private com.microsoft.playwright.Frame.WaitForURLOptions frameHasURLOptions = new com.microsoft.playwright.Frame.WaitForURLOptions();

	private Pattern urlPattern;
	private String url;
	private Predicate<String> urlPredicate;

	private Page page = null;

	public HasURL(Page page, String url) {
		this.page = page;
		this.url = url;
	}

	public HasURL(Page page, Pattern url) {
		this.page = page;
		this.urlPattern = url;
	}

	public HasURL(Page page, Predicate<String> url) {
		this.page = page;
		this.urlPredicate = url;
	}

	public HasURL timeout(Duration timeout) {
		pageWaitForUrlOptions.setTimeout(timeout.toMillis());
		return this;
	}

	@Override
	public Boolean fulfill() {
		boolean hasUrl = true;

		try {
			if (page != null) {
				if (urlPattern != null) {
					page.getPage().waitForURL(urlPattern, pageWaitForUrlOptions);
				} else if (url != null) {
					page.getPage().waitForURL(url, pageWaitForUrlOptions);
				} else if (urlPredicate != null) {
					page.getPage().waitForURL(urlPredicate, pageWaitForUrlOptions);
				} else {
					throw new IllegalStateException("Not expected");
				}
			} else {
				throw new IllegalStateException("Not implemented");
			}
		} catch (TimeoutError e) {
			hasUrl = false;
		}

		return hasUrl;
	}

}
