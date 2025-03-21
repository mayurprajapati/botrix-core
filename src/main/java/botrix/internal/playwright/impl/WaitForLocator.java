package botrix.internal.playwright.impl;

import java.time.Duration;

import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.options.WaitForSelectorState;

import botrix.internal.playwright.Fulfillable;

public class WaitForLocator implements Fulfillable<Void> {
	private Locator locator;

	private WaitForOptions options = new WaitForOptions();

	public WaitForLocator(Locator locator) {
		this.locator = locator;
	}

	public WaitForLocator setState(WaitForSelectorState state) {
		options.setState(state);
		return this;
	}

	public WaitForLocator setTimeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	@Override
	public Void fulfill() {
		locator.getLocator().waitFor(options);
		return null;
	}
}
