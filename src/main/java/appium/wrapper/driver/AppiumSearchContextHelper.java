package appium.wrapper.driver;

import org.openqa.selenium.WebElement;

import appium.wrapper.locator.AppiumLocator;

public class AppiumSearchContextHelper {
	public static AppiumWebElement findOne(AppiumLocator loc, AppiumWebElement parent, AppiumDriverWrapper driver) {
		return new AppiumWebElement(driver.driver.findElement(loc.get()), loc, parent, driver);
	}

	public static AppiumWebElements findAll(AppiumLocator loc, AppiumWebElement parent, AppiumDriverWrapper driver) {
		AppiumWebElements elements = new AppiumWebElements();
		int index = 0;
		for (WebElement el : driver.driver.findElements(loc.get())) {
			elements.add(new AppiumWebElement(el, loc, parent, driver, index));
		}
		return elements;
	}
}
