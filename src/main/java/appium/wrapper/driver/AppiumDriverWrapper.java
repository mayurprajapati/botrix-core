package appium.wrapper.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;

public class AppiumDriverWrapper {
	private RemoteWebDriver driver;

	public AppiumDriverWrapper(RemoteWebDriver driver) {
		this.driver = driver;
	}

	public AppiumWebElement findOne(AppiumLocator loc) {
		return new AppiumWebElement(driver.findElement(loc.get()), loc, null, this);
	}
}
