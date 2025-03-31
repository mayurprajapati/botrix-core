package appium.wrapper.driver;

import org.openqa.selenium.WebElement;

import appium.wrapper.locator.AppiumLocator;
import rpa.commons.collection.ListUtils;

public class AppiumSearchContextHelper {
	public static AppiumWebElement findElement(AppiumLocator loc, AppiumWebElement parent, AppiumDriverWrapper driver) {
		return new AppiumWebElement(driver.driver.findElement(loc.get()), loc, parent, driver);
	}

	public static AppiumWebElements findElements(AppiumLocator loc, AppiumWebElement parent,
			AppiumDriverWrapper driver) {
		AppiumWebElements elements = new AppiumWebElements();
		int index = 0;
		for (WebElement el : driver.driver.findElements(loc.get())) {
			elements.add(new AppiumWebElement(el,
					AppiumLocator.by(loc.getLocatorType(), loc.getLocatorName(), loc.getLocatorValue(), index), parent,
					driver, index));
		}
		return elements;
	}

	public static AppiumWebElement findElement(AppiumLocator loc, AppiumWebElement parent, AppiumDriverWrapper driver,
			int index) {
		// TODO: make it easy for xpaths? use xpath indexing?
		return ListUtils.getOrNull(findElements(loc, parent, driver), index).orElse(null);
	}
}
