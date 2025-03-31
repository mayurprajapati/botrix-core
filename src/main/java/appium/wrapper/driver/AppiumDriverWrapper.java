package appium.wrapper.driver;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.devtools.Connection;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import appium.wrapper.locator.AppiumLocator;
import botrix.utils.HasLogger;

public class AppiumDriverWrapper implements HasLogger {
	RemoteWebDriver driver;
	private Process applicationProcess;

	public AppiumDriverWrapper(RemoteWebDriver driver, Process applicationProcess) {
//		this.driver = new EventFiringDecorator<RemoteWebDriver>(new AppiumWebDriverListener()).decorate(driver);
		this.driver = driver;
		this.applicationProcess = applicationProcess;

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			quit();
		}));
	}

	public AppiumWebElement findElement(AppiumLocator loc) {
		return AppiumSearchContextHelper.findElement(loc, null, this);
	}

	public AppiumWebElements findElements(AppiumLocator loc) {
		return AppiumSearchContextHelper.findElements(loc, null, this);
	}

	@SuppressWarnings("unchecked")
	public <T> T executeScript(String script, Object... args) {
		return (T) driver.executeScript(script, args);
	}

	public boolean isPresent(AppiumLocator loc) {
		return findElementIfPresent(loc) != null;
	}

	public AppiumWebElement findElementIfPresent(AppiumLocator loc) {
		try {
			return findElements(loc).get(0);
		} catch (NoSuchElementException | IndexOutOfBoundsException e) {
			return null;
		}
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

	public Document jsoup() {
		return Jsoup.parse(driver.getPageSource());
	}

	public void waitForInvisible(AppiumLocator locator, int timeout) throws Exception {
		newWebDriverWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator.get()));
	}

	public void waitForVisible(AppiumLocator locator, int timeout) {
		newWebDriverWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator.get()));
	}

	public boolean isVisible(AppiumLocator locator) throws Exception {
		var el = findElementIfPresent(locator);
		if (el == null) {
			return false;
		}
		return el.isDisplayed();
	}

	public boolean isVisible(AppiumLocator locator, int timeout) {
		try {
			waitForVisible(locator, timeout);
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	public void type(AppiumLocator locator, CharSequence... text) {
		findElement(locator).sendKeys(text);
	}

	public void click(AppiumLocator loc) {
		findElement(loc).click();
	}

	public void clickTimes(AppiumLocator loc, int times) {
		var el = findElement(loc);
		for (int i = 0; i < times; i++) {
			el.click();
		}
		LOGGER.info("Clicked on {} {} times", loc.getLocatorName(), times);
	}

	public void clickAll(AppiumLocator loc) {
		var els = findElements(loc);
		for (var el : els) {
			el.click();
		}
	}

	public WebDriverWait newWebDriverWait(int timeout) {
		return new WebDriverWait(driver, Duration.ofSeconds(timeout));
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
