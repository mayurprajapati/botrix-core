package botrix.internal.playwright.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FrameLocator {
	private com.microsoft.playwright.FrameLocator frameLocator;
	private String name;

	public Locator locator(String selector, String name) {
		return new Locator(frameLocator.locator(selector), name);
	}

	public FrameLocator frameLocator(String selector, String name) {
		return new FrameLocator(frameLocator.frameLocator(selector), name);
	}
}
