package appium.wrapper.driver;

import java.util.Optional;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;

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
			return Optional.of(findOne(loc));
		} catch (NoSuchElementException e) {
			return Optional.empty();
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

	public void quit() {
		driver.quit();
		if (!applicationProcess.isAlive()) {
			return;
		}
		// kill process
		applicationProcess.destroyForcibly();
	}
}
