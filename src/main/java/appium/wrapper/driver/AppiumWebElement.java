package appium.wrapper.driver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import appium.wrapper.locator.AppiumLocator;
import lombok.Data;

@Data
public class AppiumWebElement {
	private WebElement element;
	private AppiumLocator locator;
	private int nthIndex = -1;
	private AppiumWebElement parent;
	private AppiumDriverWrapper driver;
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AppiumWebElement.class);

	public AppiumWebElement(WebElement element, AppiumLocator locator, AppiumWebElement parent,
			AppiumDriverWrapper driver) {
		this.element = element;
		this.locator = locator;
		this.parent = parent;
		this.driver = driver;
	}

	public AppiumWebElement(WebElement element, AppiumLocator locator, AppiumWebElement parent,
			AppiumDriverWrapper driver, int nthIndex) {
		this(element, locator, parent, driver);
		this.nthIndex = nthIndex;
	}

	public AppiumWebElement findOne(AppiumLocator loc) {
		return AppiumSearchContextHelper.findOne(loc, parent, driver);
	}

	public AppiumWebElements findAll(AppiumLocator loc) {
		return AppiumSearchContextHelper.findAll(loc, parent, driver);
	}

	public void click() {
		element.click();
		LOGGER.info("Clicked on {}", locator);
	}

	public void scrollToEnd() {
		long maxScrollHeight = getScrollHeight();
		driver.executeScript("arguments[0].scrollBy(0, %s)".formatted(maxScrollHeight), element);
	}

	public long getScrollHeight() {
		return driver.executeScript("return arguments[0].scrollHeight", element);
	}

	public boolean canBeScrolled() {
		return driver.executeScript(
				"return Math.abs(arguments[0].scrollTop - (arguments[0].scrollHeight - arguments[0].offsetHeight)) <= 3",
				element);
	}

	public void clickJs() {
		driver.executeScript("arguments[0].click()", element);
	}

	public void scrollIntoView() {
		driver.executeScript("arguments[0].scrollIntoView()", element);
	}

	public void sendKeys(String text) {
		element.sendKeys(text);
		LOGGER.info("Typed {} on {}", text, locator);
	}

	public void submit() {
		element.submit();
		LOGGER.info("Submitted on {}", locator);
	}

	public void clear() {
		element.clear();
		LOGGER.info("Cleared on {}", locator);
	}

	public String getTagName() {
		return element.getTagName();
	}

	public String getAttribute(String attr) {
		return element.getAttribute(attr);
	}

	public boolean isSelected() {
		return element.isSelected();
	}

	public boolean isEnabled() {
		return element.isEnabled();
	}

	public String getText() {
		return element.getText();
	}

	public boolean isDisplayed() {
		return element.isDisplayed();
	}

	public Dimension getSize() {
		return element.getSize();
	}

	public Rectangle getRect() {
		return element.getRect();
	}

	public String getCssValue(String propertyName) {
		return element.getCssValue(propertyName);
	}

	public String href() {
		return element.getAttribute("href");
	}
}
