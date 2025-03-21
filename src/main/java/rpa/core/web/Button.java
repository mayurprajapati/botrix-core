package rpa.core.web;

import java.time.Duration;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopRuntimeException;

public class Button {

	private static Logger logger = LoggerFactory.getLogger(Button.class);

	/**
	 * Click on element and takes screenshot after click
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @throws Exception
	 */
	public void click(String objectXpath, String objectName) throws BishopRuntimeException {
		click(objectXpath, objectName, true);
	}

	/**
	 * Wait and Click on element and takes screenshot after click
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @throws Exception
	 */
	public void click(String objectXpath, String objectName, int timeout) throws Exception {
		G.wait.elementClickable(objectXpath, objectName, timeout);
		click(objectXpath, objectName, true);
	}

	/**
	 * Click on element and takes screenshot after click
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param screenshot
	 * @throws Exception
	 */
	public void click(String objectXpath, String objectName, boolean screenshot) {
		try {
			G.elements.object(objectXpath).click();
			logger.info("Clicked on button: " + objectName);
		} catch (ElementClickInterceptedException e) {
			clickWithJs(objectXpath, objectName);
		} catch (StaleElementReferenceException e) {
			try {
				G.elements.object(objectXpath).click();
				logger.info("Clicked on button: " + objectName);
			} catch (Exception e1) {
				throw e1;
			}
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new BishopRuntimeException("Unable to click on " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new BishopRuntimeException("Unable to click on " + objectName);
		} finally {
			if (screenshot)
				Screenshot.take();
		}
	}

	/**
	 * For windows desktop app automation
	 * 
	 * @param locaType
	 * @param locator
	 * @param objectName
	 * @throws Exception
	 */
	public void click(String locaType, String locatorName, String objectName) throws Exception {
		click(locaType, locatorName, objectName, true);
	}

	public void click(String locaType, String locatorName, String objectName, boolean screenshot) throws Exception {
		try {
			G.elements.winElement(locaType, locatorName).click();
			logger.info("Clicked on button: " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locaType,
					locatorName));
			throw new Exception("Unable to click on " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new Exception("Failed to click on " + objectName);
		} finally {
			if (screenshot)
				Screenshot.take();
		}
	}

	public void click(WebElement ele, String objectName) throws Exception {
		click(ele, objectName, true);
	}

	public void click(WebElement ele, String objectName, boolean screenshot) throws Exception {
		try {
			new WebDriverWait(G.driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(ele));
			clickWithoutView(ele, objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present.", objectName));
			throw new Exception("Unable to click on " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new Exception("Unable to click on " + objectName);
		} finally {
			if (screenshot)
				Screenshot.take();
		}
	}

	public void clickWithoutView(WebElement ele, String objectName) throws Exception {
		clickWithoutView(ele, objectName, true);
	}

	public void clickWithoutView(WebElement ele, String objectName, boolean screenshot) throws Exception {
		try {
			ele.click();
			logger.info("Clicked on button : " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present.", objectName));
			throw new Exception("Unable to click on " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new Exception("Failed to click on " + objectName);
		} finally {
			if (screenshot) {
				Screenshot.take();
			}
		}
	}

	public void clickWithJs(String objectXpath, String objectName) {
		try {
			G.jse.executeScript("arguments[0].click();", G.elements.object(objectXpath));
			logger.info("Clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new BishopRuntimeException("Failed to click on " + objectName);
		} finally {
//			Screenshot.take();
		}
	}

	public void clickWithJs(WebElement element, String objectName) throws Exception {
		try {
			G.jse.executeScript("arguments[0].click();", element);
			logger.info("Clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new Exception("Failed to click on " + objectName);
		} finally {
//			Screenshot.take();
		}
	}

	public void clickElementFromList(String objectXpath, String objectName) throws Exception {
		try {
			G.elements.objects(objectXpath).get(G.elements.objects(objectXpath).size() - 1).click();
			logger.info("Clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception("Failed to click on " + objectName);
		} finally {
//			Screenshot.take();
		}
	}

	public void doubleClick(String objectXpath, String objectName) throws Exception {
		try {
			new Actions(G.driver).moveToElement(G.elements.object(objectXpath))
					.doubleClick(G.elements.object(objectXpath)).build().perform();
			logger.info("Double clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to double click on " + objectName, e);
			throw new Exception("Failed to double click on " + objectName);
		} finally {
//			Screenshot.take();
		}
	}

	/**
	 * This method right clicks on the element
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @throws Exception
	 */
	public void rightClick(String objectXpath, String objectName) throws Exception {
		try {
			WebElement eleToRightClick = G.elements.object(objectXpath);
			new Actions(G.driver).moveToElement(eleToRightClick).contextClick(G.elements.object(objectXpath)).build()
					.perform();
			logger.info("Double clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to right click on " + objectName, e);
			throw new Exception("Failed to right click on " + objectName);
		} finally {
			Screenshot.take();
		}
	}

	public void doubleClick(String locType, String objectXpath, String objectName) throws Exception {
		doubleClick(locType, objectXpath, objectName, null, null);
	}

	/**
	 * For windows automation
	 * 
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public void doubleClick(String locType, String objectXpath, String objectName, Integer x, Integer y)
			throws Exception {
		try {
			WebElement ele = G.elements.winElement(locType, objectXpath);
			doubleClick(ele, objectName, x, y);
		} catch (Exception e) {
			logger.error("Failed to double click on " + objectName, e);
			throw new Exception("Failed to double click on " + objectName);
		}
	}

	/**
	 * Double click with offset
	 * 
	 * @param ele
	 * @param objectName
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public void doubleClick(WebElement ele, String objectName, Integer x, Integer y) throws Exception {
		try {
			Actions ac = new Actions(G.driver);
			if (x != null && y != null) {
				ac.moveToElement(ele, x, y);
			} else {
				ac.moveToElement(ele);
			}
			ac.doubleClick().build().perform();
			logger.info("Double clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to double click on " + objectName, e);
			throw new Exception("Failed to double click on " + objectName);
		}
	}

	/**
	 * Click using offset
	 * 
	 * @param ele
	 * @param objectName
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public void click(WebElement ele, String objectName, Integer x, Integer y) throws Exception {
		try {
			ele.click();
			Actions ac = new Actions(G.driver);
			ac.moveToElement(ele, x, y).click().build().perform();
			logger.info("Clicked on button: " + objectName);
		} catch (Exception e) {
			logger.error("Failed to click on " + objectName, e);
			throw new Exception("Failed to click on " + objectName);
		}
	}

	/**
	 * Clicks on child element. Uses xpath as locator.
	 * 
	 * @param parentXpath
	 * @param childXpath
	 * @param elementName
	 * @throws Exception
	 */
	public void clickOnChild(String parentXpath, String childXpath, String elementName) throws Exception {
		clickOnChild(Elements.XPATH, parentXpath, Elements.XPATH, childXpath, elementName);
	}

	/**
	 * Clicks on child element.
	 * 
	 * @param parentLocatorType
	 * @param parentlocator
	 * @param childLocatorType
	 * @param childLocator
	 * @param elementTitle
	 * @throws Exception
	 */
	public void clickOnChild(String parentLocatorType, String parentlocator, String childLocatorType,
			String childLocator, String elementTitle) throws Exception {
		WebElement parent = G.elements.winElement(parentLocatorType, parentlocator);
		WebElement child = G.elements.getInnerElement(parent, childLocatorType, childLocator, true);
		G.button.click(child, "Child element");
		logger.info("clicked on {}", elementTitle);
	}

	/**
	 * Clicks on child element of provided element. Uses xpath as locator
	 * 
	 * @param parent
	 * @param childXpath
	 * @param elementTitle
	 * @throws Exception
	 */
	public void clickOnChild(WebElement parent, String childXpath, String elementTitle) throws Exception {
		clickOnChild(parent, Elements.XPATH, childXpath, elementTitle);
	}

	/**
	 * Clicks on child element of provided element.
	 * 
	 * @param parent
	 * @param childLocatorType
	 * @param childLocator
	 * @param elementName
	 * @throws Exception
	 */
	public void clickOnChild(WebElement parent, String childLocatorType, String childLocator, String elementName)
			throws Exception {
		WebElement child = G.elements.getInnerElement(parent, childLocatorType, childLocator, true);
		G.button.click(child, "Child element");
		logger.info("clicked on {}", elementName);
	}

}
