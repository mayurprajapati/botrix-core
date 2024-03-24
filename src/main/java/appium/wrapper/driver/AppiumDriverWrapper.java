package appium.wrapper.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
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

	public List<AppiumWebElement> findAll(AppiumLocator loc) {
		List<AppiumWebElement> elements = new ArrayList<>();
		int index = 0;
		for (WebElement el : driver.findElements(loc.get())) {
			elements.add(new AppiumWebElement(el, loc, null, this, index));
		}
		return elements;
	}

	public void click(AppiumLocator loc) {
		findOne(loc).click();
	}

	@SuppressWarnings("unchecked")
	public <T> T executeScript(String script, Object... args) {
		return (T) driver.executeScript(script, args);
	}

	public void sendKeys(AppiumLocator loc, String value) {
		findOne(loc).sendKeys(value);
	}

	public boolean isPresent(AppiumLocator loc) {
		return findOneIfPresent(loc).isPresent();
	}

	public List<String> hrefs(AppiumLocator loc) {
		return findAll(loc).stream().map(AppiumWebElement::href).toList();
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
