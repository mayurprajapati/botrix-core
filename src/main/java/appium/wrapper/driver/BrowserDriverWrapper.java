package appium.wrapper.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

public class BrowserDriverWrapper extends AppiumDriverWrapper {

	public BrowserDriverWrapper(RemoteWebDriver driver, Process applicationProcess) {
		super(driver, applicationProcess);
	}

}
