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

public class AppiumDriverWrapper {
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
		return findOneIfPresent(locator).map(el -> el.isDisplayed()).orElse(false);
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
		findOne(locator).sendKeys(text);
	}

	public void click(AppiumLocator loc) {
		findOne(loc).click();
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
