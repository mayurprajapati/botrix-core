package appium.wrapper.driver;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.devtools.Connection;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;
import grpc.bridge.python.PythonBridgeClient;

public class AppiumDriverWrapper {
	RemoteWebDriver driver;
	private Process applicationProcess;

	public AppiumDriverWrapper(RemoteWebDriver driver, Process applicationProcess) {
		this.driver = driver;
		this.applicationProcess = applicationProcess;
	}

	public AppiumWebElement findOne(AppiumLocator loc) {
		return AppiumSearchContextHelper.findOne(loc, null, this);
	}

	public AppiumWebElements findAll(AppiumLocator loc) {
		return AppiumSearchContextHelper.findAll(loc, null, this);
	}

	@SuppressWarnings("unchecked")
	public <T> T executeScript(String script, Object... args) {
		return (T) driver.executeScript(script, args);
	}

	public boolean isPresent(AppiumLocator loc) {
		return findOneIfPresent(loc).isPresent();
	}

	public Optional<AppiumWebElement> findOneIfPresent(AppiumLocator loc) {
		try {
			return Optional.of(findAll(loc).get(0));
		} catch (NoSuchElementException | IndexOutOfBoundsException e) {
			return Optional.empty();
		}
	}

	public void resolveRecaptcha() {
		URI url = getDevtoolsUri();
		PythonBridgeClient.resolveRecaptcha(url.getHost(), url.getPort());
	}

	public URI getDevtoolsUri() {
		try {
			Field connectionField = FieldUtils.getField(org.openqa.selenium.devtools.DevTools.class, "connection",
					true);
			Field urlField = FieldUtils.getField(org.openqa.selenium.devtools.Connection.class, "url", true);
			Connection connection = (Connection) connectionField.get(((HasDevTools) driver).getDevTools());
			URI url = new URI((String) urlField.get(connection));
			return url;
		} catch (IllegalAccessException | IllegalArgumentException | URISyntaxException e) {
			throw new RuntimeException("Unable to get Browser Devtools URI", e);
		}
	}

	public void get(String url) {
		driver.get(url);
	}

	public Document jsoup() {
		return Jsoup.parse(driver.getPageSource());
	}

	public Set<Cookie> getCookies() {
		return driver.manage().getCookies();
	}

	public boolean isRecaptchaAvailable() {
		return isPresent(AppiumLocator.byXpath("reCAPTCHA", "//iframe[@title=\"reCAPTCHA\"]"));
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
