package appium.wrapper.driver;

import java.util.function.Consumer;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserDriverBuilder {
	private DesiredCapabilities caps = new DesiredCapabilities();
	private ChromeOptions options = new ChromeOptions();

	public BrowserDriverBuilder wdm(Consumer<WebDriverManager> setup) {
		var wdm = WebDriverManager.chromedriver();
		setup.accept(wdm);
		return this;
	}

	public BrowserDriverBuilder capabilities(DesiredCapabilities caps) {
		this.caps = caps;
		return this;
	}

	public BrowserDriverBuilder headless() {
		options.addArguments("headless").addArguments("disable-gpu");
		return this;
	}

	public AppiumDriverWrapper buildChrome() {
		return new ChromeDriverWrapper(new ChromeDriver(options));
	}
}
