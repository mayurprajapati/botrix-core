package botrix.internal.playwright.impl;

import java.util.Map;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Playwright.CreateOptions;

import botrix.internal.playwright.Fulfillable;

public class PlaywrightCreateOptions implements Fulfillable<Playwright> {
	private CreateOptions options = new CreateOptions();

	public PlaywrightCreateOptions setEnv(Map<String, String> env) {
		options.setEnv(env);
		return this;
	}

	@Override
	public Playwright fulfill() {
		return Playwright.create(options);
	}
}
