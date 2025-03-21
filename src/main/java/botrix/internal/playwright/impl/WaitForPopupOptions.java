package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.function.Predicate;

import botrix.internal.playwright.Fulfillable;

public class WaitForPopupOptions implements Fulfillable<Page> {
	private com.microsoft.playwright.Page.WaitForPopupOptions options = new com.microsoft.playwright.Page.WaitForPopupOptions();
	private Runnable callback;
	private Page page;

	public WaitForPopupOptions(Page page, Runnable callback) {
		this.callback = callback;
		this.page = page;
	}

	public WaitForPopupOptions timeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	public WaitForPopupOptions predicate(Predicate<Page> predicate) {
		options.setPredicate((page) -> predicate.test(new Page(page)));
		return this;
	}

	@Override
	public Page fulfill() {
		return new Page(page.getPage().waitForPopup(options, callback));
	}
}
