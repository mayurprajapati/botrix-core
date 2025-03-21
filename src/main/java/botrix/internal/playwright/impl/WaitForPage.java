package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.function.Predicate;

import com.microsoft.playwright.BrowserContext.WaitForPageOptions;
import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.options.WaitForSelectorState;

import botrix.internal.playwright.Fulfillable;

public class WaitForPage implements Fulfillable<Void> {

	private Page page;

	private WaitForPageOptions options = new WaitForPageOptions();

	public WaitForPage(Page page) {
		this.page = page;
	}

	public WaitForPage setTimeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	public WaitForPage setPredicate(Predicate<Page> predicate) {
		options.setPredicate((page) -> {
			return predicate.test(PlaywrightUtils.playwright(page));
		});
		return this;
	}

	@Override
	public Void fulfill() {
//		page.getPage().
		return null;
	}
}
