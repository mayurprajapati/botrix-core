package appium.wrapper.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ChromeDriverWrapper<T extends ChromeDriver> extends BrowserDriverWrapper<RemoteWebDriver> {

	public ChromeDriverWrapper(RemoteWebDriver driver) {
		super(driver);
	}

}
