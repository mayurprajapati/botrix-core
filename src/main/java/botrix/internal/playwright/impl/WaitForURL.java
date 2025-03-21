package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import botrix.internal.playwright.Fulfillable;

public class WaitForURL implements Fulfillable<Void> {
	private com.microsoft.playwright.Page.WaitForURLOptions pageWaitForUrlOptions = new com.microsoft.playwright.Page.WaitForURLOptions();
	private com.microsoft.playwright.Frame.WaitForURLOptions frameWaitForUrlOptions = new com.microsoft.playwright.Frame.WaitForURLOptions();

	private Pattern urlPattern;
	private String url;
	private Predicate<String> urlPredicate;

	private Page page = null;

	public WaitForURL(Page page, String url) {
		this.page = page;
		this.url = url;
	}

	public WaitForURL(Page page, Pattern url) {
		this.page = page;
		this.urlPattern = url;
	}

	public WaitForURL(Page page, Predicate<String> url) {
		this.page = page;
		this.urlPredicate = url;
	}

	public WaitForURL timeout(Duration timeout) {
		pageWaitForUrlOptions.setTimeout(timeout.toMillis());
		return this;
	}

	@Override
	public Void fulfill() {
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
		}
		return null;
	}
}
