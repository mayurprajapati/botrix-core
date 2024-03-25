package appium.wrapper.driver;

import java.util.ArrayList;
import java.util.List;

import appium.wrapper.locator.AppiumLocator;

public class AppiumWebElements extends ArrayList<AppiumWebElement> {
	private static final long serialVersionUID = 1L;

	public void clickAll() {
		for (AppiumWebElement el : this) {
			el.click();
		}
	}

	public List<String> hrefs(AppiumLocator loc) {
		return stream().map(AppiumWebElement::href).toList();
	}
}
