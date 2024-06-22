package appium.wrapper.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverWrapper extends AppiumDriverWrapper {
	private ChromeOptions chromeOptions;

	public ChromeDriverWrapper(ChromeDriver driver, Process browserProcess, ChromeOptions options,
			ChromeDriverBuilder chromeDriverBuilder) {
		super(driver, browserProcess);
		this.chromeOptions = options;
	}
}
