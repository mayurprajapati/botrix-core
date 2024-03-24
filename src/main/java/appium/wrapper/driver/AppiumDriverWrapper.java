package appium.wrapper.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;

public class AppiumDriverWrapper {
	private RemoteWebDriver driver;
	private Process applicationProcess;

	public AppiumDriverWrapper(RemoteWebDriver driver, Process applicationProcess) {
		this.driver = driver;
		this.applicationProcess = applicationProcess;
	}

	public AppiumWebElement findOne(AppiumLocator loc) {
		return new AppiumWebElement(driver.findElement(loc.get()), loc, null, this);
	}

	public void click(AppiumLocator loc) {
		findOne(loc).click();
	}

	public void sendKeys(AppiumLocator loc, String value) {
		findOne(loc).sendKeys(value);
	}

	public void get(String url) {
		driver.get(url);
	}

	public void quit() {
		driver.quit();
		if (!applicationProcess.isAlive()) {
			return;
		}
		// kill process
		applicationProcess.destroyForcibly();
	}
}
