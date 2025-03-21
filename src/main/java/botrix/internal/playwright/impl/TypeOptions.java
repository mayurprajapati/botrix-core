package botrix.internal.playwright.impl;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import botrix.internal.playwright.Fulfillable;

public class TypeOptions implements Fulfillable<Void> {
	private static final Logger logger = LoggerFactory.getLogger(TypeOptions.class);

	private com.microsoft.playwright.Locator.TypeOptions options = new com.microsoft.playwright.Locator.TypeOptions();
	private Locator locator;
	private String text;

	public TypeOptions(String text, Locator locator) {
		this.text = text;
		this.locator = locator;
	}

	public TypeOptions timeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	public TypeOptions delay(Duration delay) {
		options.setDelay(delay.toMillis());
		return this;
	}

	public TypeOptions noWaitAfter(Boolean noWaitAfter) {
		options.setNoWaitAfter(noWaitAfter);
		return this;
	}

	@Override
	public Void fulfill() {
		try {
			String type = locator.getLocator().getAttribute("type");
			locator.getLocator().type(text, options);
			if (StringUtils.equalsIgnoreCase(type, "password")) {
				text = "********";
			}
			logger.info("Typed '{}' in '{}'", text, locator.getName());
		} catch (Exception e) {
			logger.info("Error in typing '{}' in '{}'", text, locator.getName());
		}
		return null;
	}
}
