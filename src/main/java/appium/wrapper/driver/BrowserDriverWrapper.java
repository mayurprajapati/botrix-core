package appium.wrapper.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

public class BrowserDriverWrapper<T extends RemoteWebDriver> extends AppiumDriverWrapper {

	public BrowserDriverWrapper(RemoteWebDriver driver) {
		super(driver);
	}

}
