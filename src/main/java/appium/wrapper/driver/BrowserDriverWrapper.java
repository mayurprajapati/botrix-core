package appium.wrapper.driver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;
import grpc.bridge.python.PythonBridgeClient;
import lombok.SneakyThrows;

public class BrowserDriverWrapper extends AppiumDriverWrapper {
	private BrowserDriverBuilderOptions options;

	public BrowserDriverWrapper(RemoteWebDriver driver, Process applicationProcess,
			BrowserDriverBuilderOptions options) {
		super(driver, applicationProcess);
		this.options = options;
	}

	public Set<Cookie> getCookies() {
		return driver.manage().getCookies();
	}

	public boolean isRecaptchaAvailable() {
		return isPresent(AppiumLocator.byXpath("reCAPTCHA", "//iframe[@title=\"reCAPTCHA\"]"));
	}

	public void resolveRecaptcha() {
		URI url = getDevtoolsUri();
		PythonBridgeClient.resolveRecaptcha(url.getHost(), url.getPort());
	}

	public void get(String url) {
		driver.get(url);
	}

	@SneakyThrows
	public void quit() {
		super.quit();
		if (!options.isKeepUserDataDir()) {
			for (int i = 0; i < 5; i++) {
				try {
					FileUtils.forceDelete(new File(options.getUserDataDir()));
					return;
				} catch (IOException e) {
					if (StringUtils.containsAnyIgnoreCase(e.getMessage(),
							"DOS or POSIX file operations not available for", "Cannot delete file")) {
						Thread.sleep(100);
					} else {
						throw e;
					}
				}
			}
		}
	}

}
