package botrix.internal.playwright.impl;

import java.time.Duration;
import java.util.List;

import com.microsoft.playwright.Locator.HoverOptions;
import com.microsoft.playwright.options.KeyboardModifier;
import com.microsoft.playwright.options.Position;

import botrix.internal.playwright.Fulfillable;

public class LocatorHoverOptions implements Fulfillable<Void> {
	private Locator locator;
	private HoverOptions options;

	public LocatorHoverOptions(Locator locator) {
		this.locator = locator;
	}

	public LocatorHoverOptions force(boolean force) {
		options.setForce(force);
		return this;
	}

	public LocatorHoverOptions modifiers(List<KeyboardModifier> modifiers) {
		options.setModifiers(modifiers);
		return this;
	}

	public LocatorHoverOptions setPosition(double x, double y) {
		return setPosition(new Position(x, y));
	}

	public LocatorHoverOptions setPosition(Position position) {
		options.setPosition(position);
		return this;
	}

	public LocatorHoverOptions setTimeout(Duration timeout) {
		options.setTimeout(timeout.toMillis());
		return this;
	}

	public LocatorHoverOptions setTrial(boolean trial) {
		this.setTrial(trial);
		return this;
	}

	@Override
	public Void fulfill() {
		locator.getLocator().hover(options);
		return null;
	}

}
