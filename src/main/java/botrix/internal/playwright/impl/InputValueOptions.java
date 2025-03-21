package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.Optional;

import com.microsoft.playwright.TimeoutError;

import botrix.internal.playwright.Fulfillable;

public class InputValueOptions implements Fulfillable<Optional<String>> {
	private Locator locator;
	private com.microsoft.playwright.Locator.InputValueOptions options = new com.microsoft.playwright.Locator.InputValueOptions();

	public InputValueOptions(Locator locator) {
		this.locator = locator;
	}

	public InputValueOptions setTimeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	@Override
	public Optional<String> fulfill() {
		return getInputValue();
	}

	private Optional<String> getInputValue() {
		try {
			String value = locator.getLocator().inputValue(options);
			return Optional.ofNullable(value);
		} catch (TimeoutError e) {
			return Optional.empty();
		}
	}
}
