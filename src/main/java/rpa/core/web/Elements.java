package rpa.core.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByTagName;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import io.appium.java_client.AppiumBy.ByAccessibilityId;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.windows.WindowsDriver;
import rpa.core.driver.Browser;
import rpa.core.driver.G;

public class Elements {

	private static Logger logger = LoggerFactory.getLogger(Elements.class);
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String XPATH = "xpath";
	public static final String AUTOMATION_ID = "automationId";
	public static final String CLASS_NAME = "className";
	public static final String TAG_NAME = "tagName";
	public static final String LINK_TEXT = "linkText";
	public static final String PARTIAL_LINK_TEXT = "partialLinkText";
	public static final String CSS_SELECTOR = "cssSelector";

	public List<WebElement> returnWebElements(String locType, String locator) {
		return findElements(byLocator(locType, locator));
	}

	public WebElement winElement(String locType, String locator) {
		WebElement e = null;
		By by = null;
		if (locType.trim().equals(AUTOMATION_ID)) {
//			e = ((WindowsDriver<WebElement>) G.driver).findElementByAccessibilityId(locator);
			e = ((AppiumDriver) G.driver).findElement(ByAccessibilityId.accessibilityId(locator));
		} else {
			by = byLocator(locType, locator);
		}
		
		if (e == null && by != null) {
			e = G.driver.findElement(by);
		}
		return e;
	}

	public By byLocator(String locType, String locator) {
		By by = null;

		switch (locType.trim()) {
		case ID:
			by = By.id(locator);
			break;
		case TAG_NAME:
			by = By.tagName(locator);
			break;
		case XPATH:
			by = By.xpath(locator);
			break;
		case CSS_SELECTOR:
			by = By.cssSelector(locator);
			break;
		case CLASS_NAME:
			by = By.className(locator);
			break;
		case NAME:
			by = By.name(locator);
			break;
		case LINK_TEXT:
			by = By.linkText(locator);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(locator);
			break;
		default:
		}
		return by;
	}

	/**
	 * This method will set implicit timeout while finding element
	 * 
	 * @param locType
	 * @param locator
	 * @param timeout
	 * @return
	 */
	public WebElement winElement(String locType, String locator, int timeout) {
		try {
			G.window.implicitWait(timeout);
			WebElement e = winElement(locType, locator);
			return e;
		} catch (Exception e) {
			if (timeout == Browser.DEFAULT_DRIVER_TIMEOUT)
				throw e;
			else
				logger.info("Element {} | {} not found in {} seconds", locType, locator, timeout);
		} finally {
			G.window.implicitWaitDefault();
		}
		return null;
	}

	/**
	 * This will return list of WebElements matching given locator. ***Use only for
	 * Windows Apps****
	 * 
	 * @param locType
	 * @param locator
	 * @return List<WebElement>
	 */
	public List<WebElement> winElements(String locType, String locator) {
		List<WebElement> e = null;
		By by = null;
		switch (locType.trim()) {
		case ID:
			e = ((AppiumDriver) G.driver).findElements(ByAccessibilityId.id(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsById(locator);
			break;
		case TAG_NAME:
			e = ((AppiumDriver) G.driver).findElements(ByTagName.tagName(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByTagName(locator);
			break;
		case XPATH:
			e = ((AppiumDriver) G.driver).findElements(By.xpath(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByXPath(locator);
			break;
		case CSS_SELECTOR:
			e = ((AppiumDriver) G.driver).findElements(By.cssSelector(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByCssSelector(locator);
			break;
		case CLASS_NAME:
			e = ((AppiumDriver) G.driver).findElements(ByTagName.className(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByClassName(locator);
			break;
		case NAME:
			e = ((AppiumDriver) G.driver).findElements(By.name(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByName(locator);
			break;
		case LINK_TEXT:
			e = ((AppiumDriver) G.driver).findElements(By.linkText(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByLinkText(locator);
			break;
		case PARTIAL_LINK_TEXT:
			e = ((AppiumDriver) G.driver).findElements(ByTagName.partialLinkText(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByPartialLinkText(locator);
			break;
		case AUTOMATION_ID:
			e = ((AppiumDriver) G.driver).findElements(ByAccessibilityId.accessibilityId(locator));
//			e = ((WindowsDriver<WebElement>) G.driver).findElementsByAccessibilityId(locator);
			break;
		default:
		}
//		if(e == null && by != null) {
//			e = Global.driver.findElements(by);
//		}
		return e;
	}

	/**
	 * Finds element within source element.
	 * 
	 * @param element
	 * @param locatorType
	 * @param locator
	 * @return
	 */
	public WebElement innerEleOfParentEle(WebElement element, String locatorType, String locator) {
		return getInnerElement(element, locatorType, locator, true);
	}

	/**
	 * Finds element within source element.
	 * 
	 * @param element
	 * @param locatorType
	 * @param locator
	 * @return
	 */
	public WebElement innerEleOfParentEle(WebElement element, String locatorType, String locator, int timout) {
		return getInnerElement(element, locatorType, locator, true, timout);
	}

	/**
	 * Finds element within source element.
	 * 
	 * @param element     - source element
	 * @param locatorType
	 * @param locator     - only xpath, name and automation ID supported
	 * @param errorLog    - flag to see error logs
	 * @return
	 */
	public WebElement getInnerElement(WebElement element, String locatorType, String locator, boolean errorLog) {
		return getInnerElement(element, locatorType, locator, errorLog, Browser.DEFAULT_DRIVER_TIMEOUT);
	}

	/**
	 * Finds element within source element.
	 * 
	 * @param element     - source element
	 * @param locatorType
	 * @param locator     - only xpath, name and automation ID supported
	 * @param errorLog    - flag to see error logs
	 * @return
	 */
	public WebElement getInnerElement(WebElement element, String locatorType, String locator, boolean errorLog,
			int timeout) {
		WebElement innerElement = null;
		G.window.implicitWait(timeout);
		try {
			if (element != null) {
				switch (locatorType.trim()) {
				case XPATH:
					innerElement = element.findElement(By.xpath(locator));
					break;
				case NAME:
					innerElement = element.findElement(By.name(locator));
					break;
				case TAG_NAME:
					innerElement = element.findElement(By.tagName(locator));
					break;
				case AUTOMATION_ID:
//					innerElement = ((WindowsDriver<WebElement>) ((RemoteWebElement) element).getWrappedDriver())
//							.findElementByAccessibilityId(locator);
					innerElement = (WebElement) (element).findElements(ByAccessibilityId.accessibilityId(locator));
					break;
				default:
				}
			} else if (errorLog) {
				logger.warn("Source element is Null");
			}
		} catch (Exception e) {
			if (errorLog)
				logger.error(String.format("Error finding child element by %s: %s", locatorType, locator), e);
		} finally {
			G.window.implicitWaitDefault();
		}
		return innerElement;
	}

	public List<WebElement> innerElementsOfParentEle(WebElement parentElement, String locatorType, String locator)
			throws Exception {
		return innerElementsOfParentEle(parentElement, locatorType, locator, Browser.DEFAULT_DRIVER_TIMEOUT);
	}

	/**
	 * Returns List<WebElement> under a parent element matching provided locator
	 * locator
	 * 
	 * @param parentElement
	 * @param locatorType
	 * @param locator
	 * @return List<WebElement>
	 * @throws Exception
	 */
	public List<WebElement> innerElementsOfParentEle(WebElement parentElement, String locatorType, String locator,
			int timeout) throws Exception {
		G.window.implicitWait(timeout);
		List<WebElement> e = null;
		try {
			switch (locatorType.trim()) {
			case XPATH:
				e = parentElement.findElements(By.xpath(locator));
				break;
			case NAME:
				e = parentElement.findElements(By.name(locator));
				break;
			case AUTOMATION_ID:
//				e = ((WindowsDriver<WebElement>) ((RemoteWebElement) parentElement).getWrappedDriver())
//						.findElementsByAccessibilityId(locator);
				e = (parentElement).findElements(ByAccessibilityId.accessibilityId(locator));
				break;
			default:
				throw new Exception("Locator does not supported");
			}
		} catch (Exception e1) {
			throw e1;
		} finally {
			G.window.implicitWaitDefault();
		}
		return e;
	}

	public List<WebElement> findElements(By by) {
//		if ("angular".equals(G.testProps.getProperty("webapptype"))) {
//			G.wait.forAngularLoad();
//		}
		List<WebElement> elements = new ArrayList<>();
		try {
			elements = G.driver.findElements(by);
		} catch (NoSuchElementException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return elements;
	}

	public WebElement findElement(By by) {
		return findElement(by, true);
	}

	public WebElement findElement(By by, boolean printLog) {
//		if ("angular".equals(G.testProps.getProperty("webapptype"))) {
//			G.wait.forAngularLoad();
//		}
		WebElement element = null;
		try {
			element = G.driver.findElement(by);
		} catch (NoSuchElementException e) {
			if (printLog) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			if (printLog) {
				logger.error(e.getMessage());
			}
		}
		return element;
	}

	public WebElement object(By by) {
		return findElement(by);
	}

	public List<WebElement> objects(By bys) {
		return findElements(bys);
	}

	public WebElement object(String objectXpath, int timeout) {
		G.window.implicitWait(timeout);
		WebElement e = findElement(By.xpath(objectXpath), false);
		G.window.implicitWaitDefault();
		return e;
	}

	public WebElement object(String objectXpath) {
		G.window.implicitWaitDefault();
		return findElement(By.xpath(objectXpath));
	}

	public List<WebElement> objects(String objectXpath, int timeout) {
		List<WebElement> objects = new ArrayList<>();
		G.window.implicitWait(timeout);
		objects = findElements(By.xpath(objectXpath));
		G.window.implicitWaitDefault();
		return objects;
	}

	public List<WebElement> objects(String objectXpath) {
		return findElements(By.xpath(objectXpath));
	}

	public WebElement findSimilarByTextOnPage(String text) {
		G.window.implicitWait(2);
		WebElement e = findElement(
				byLocator(Elements.XPATH, "//*[contains(text(),'$$textToFind$$')]".replace("$$textToFind$$", text)), false);
		G.window.implicitWaitDefault();
		return e;
	}

	public WebElement findByTextOnPage(String text) {
		G.window.implicitWait(2);
		WebElement e = findElement(byLocator(Elements.XPATH, "//*[text()='$$textToFind$$']".replace("$$textToFind$$", text)),
				false);
		G.window.implicitWaitDefault();
		return e;
	}

}
