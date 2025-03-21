package rpa.core.web;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.Browser;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopRuntimeException;

public class Wait {

	private static Logger logger = LoggerFactory.getLogger(Wait.class);

	/**
	 * Thread sleep
	 * 
	 * @param secs - duration in seconds
	 */
	public void sleep(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}

	/**
	 * Thread sleep
	 * 
	 * @param duration - in milliseconds
	 */
	public void sleepforMilliseconds(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}

	public void forPageToLoad() {
		forPageToLoad(Browser.DEFAULT_DRIVER_TIMEOUT);
	}

	public void forPageToLoad(int secs) {
		try {
			new WebDriverWait(G.driver, Duration.ofSeconds(secs)).until(webDriver -> ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Exception e) {
			logger.error("Failed to wait for page to load");
		}
	}

	public boolean elementClickable(String objectXpath, String objectName) throws Exception {
		return elementClickable(objectXpath, objectName, 30);
	}

	public boolean elementClickable(String objectXpath, String objectName, int timeout) throws Exception {
		try {
			WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(objectXpath)));
			return true;
		} catch (Exception e) {
			logger.error("Could not wait for element to be clickable: " + objectName);
		}
		return false;
	}

	public void validateUrlContains(String url, int timeout) {
		if (!urlContains(url, timeout))
			throw new BishopRuntimeException(format("Window does not contain url '%s'", url));
	}

	public boolean urlContains(String url, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.urlContains(url));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	public boolean isPresent(String xpath, String name) {
		return isPresent(xpath, name, 0);
	}

	public boolean isPresent(String xpath, String name, int timeout) {
		return fluentForElement(xpath, name, timeout) != null;
	}

	public boolean elementClickable(WebElement ele, String objectName, int timeout) throws Exception {
		try {
			WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeClickable(ele));
			return true;
		} catch (Exception e) {
			logger.error("Could not wait for element to be clickable: " + objectName);
		}
		return false;
	}

	public WebElement fluentForElement(String objectXpath, String objectName) {
		return fluentForElement(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT);
	}

	public WebElement fluentForElement(String objectXpath, String objectName, int timeout) {
		return fluentForElement(objectXpath, objectName, timeout, true);
	}

	public WebElement fluent(String objectXpath, String objectName) {
		return fluentForElement(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, false);
	}

	public WebElement fluentForElement(String objectXpath, String objectName, int timeout, boolean skipLog) {
		try {
			return fluentWait(timeout).until(ExpectedConditions.visibilityOf(G.elements.object(objectXpath, timeout)));
		} catch (Exception e) {
			if (!skipLog) {
				logger.info(String.format("Element %s not visible", objectName));
			}
			return null;
		}
	}

	public WebElement fluentForElement(String locType, String locatorName, String objectName, int timeout) {
		G.window.implicitWait(0);
		try {
			return fluentWait(timeout)
					.until(ExpectedConditions.visibilityOf(G.elements.winElement(locType, locatorName, timeout)));
		} catch (Exception e) {
			logger.info(String.format("Element %s not visible", objectName));
			return null;
		} finally {
			G.window.implicitWaitDefault();
		}
	}

	/**
	 * It waits for an element. Tries to find element in every seconds
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param timeout     - Maximum timeout in seconds
	 * @return - Returns the element if found
	 */
	public WebElement fluentForElementHardWait(String locType, String locatorName, String objectName, int timeout) {
		G.window.implicitWait(0);
		try {
			WebElement ele = null;
			for (int i = 0; i < timeout; i++) {
				try {
					G.wait.sleep(1);
					ele = G.elements.winElement(locType, locatorName);
					if (ele != null)
						return ele;
				} catch (Exception e) {
					logger.info(String.format("Element %s not visible in %s seconds", objectName, i));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			G.window.implicitWaitDefault();
		}
		return null;
	}

	private FluentWait<RemoteWebDriver> fluentWait(int seconds) {
		return new FluentWait<>(G.driver).withTimeout(Duration.ofSeconds(seconds)).pollingEvery(Duration.ofMillis(200))
				.ignoring(NoSuchElementException.class, TimeoutException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	public WebElement fluentForElementWithoutWait(String objectXpath, String objectName) {
		try {
			return fluentWait(0).until(ExpectedConditions.visibilityOf(G.elements.object(objectXpath)));
		} catch (Exception e) {
			logger.info(String.format("Element %s not visible", objectName));
			return null;
		}
	}

	public WebElement fluentForElementForSecs(String objectXpath, String objectName, int seconds) {
		try {
			return fluentWait(seconds).until(ExpectedConditions.visibilityOf(G.elements.object(objectXpath, seconds)));
		} catch (Exception e) {
			logger.info(String.format("Element %s not visible in %s seconds", objectName, seconds));
			logger.error(e.getMessage());
			return null;
		}
	}

	public void forAngularLoad() {
		WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(15));
		String angularReadyScript = "return (window.angular !== undefined) && (angular.element(document.body).injector() !== undefined) && (angular.element(document.body).injector().get('$http').pendingRequests.length === 0)";
		ExpectedCondition<Boolean> angularLoad = driver -> Boolean
				.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());
		boolean angularReady = Boolean.parseBoolean(G.jse.executeScript(angularReadyScript).toString());
		if (!angularReady) {
			wait.until(angularLoad);
		} else {
		}
	}

	/**
	 * This method wait for the element to become invisible.
	 * 
	 * @param xpath
	 * @param objectName
	 * @param timeout    - Max timeout in seconds till when it wait for element to
	 *                   become invisible
	 * @throws Exception if Element is visible even after waiting for max timeout
	 *                   period
	 */
	public void waitForInvisibilityOfElement(String xpath, String objectName, int timeout) throws Exception {
		logger.info(String.format("Waiting for element '%s' to be invisible", objectName));
		boolean elementDisappeared = false;
		int totalTimeWaited = 0;
		while (totalTimeWaited < timeout) {
			List<WebElement> elements = G.elements.objects(xpath, 0);
			if (elements.size() == 0) {
				elementDisappeared = true;
				break;
			} else {
				boolean elementDisplayed = true;
				try {
					elementDisplayed = G.elements.object(xpath, 0).isDisplayed();
				} catch (Exception e) {
					// Ignore exception
				}
				if (!elementDisplayed) {
					elementDisappeared = true;
					break;
				} else {
					G.wait.sleep(2);
					totalTimeWaited = totalTimeWaited + 2;
				}
			}
		}
		if (!elementDisappeared) {
			throw new Exception(
					String.format("Element '%s' is not invisible after waited for %s seconds", objectName, timeout));
		} else {
			logger.info(String.format("Element '%s' is invisible", objectName));
		}
	}

	public void waitForInvisibilityOfWinElement(String locatorType, String locator, String objectName, int timeout)
			throws Exception {
		logger.info(String.format("Waiting for element '%s' to be invisible", objectName));
		boolean elementDisappeared = false;
		int totalTimeWaited = 0;
		while (totalTimeWaited < timeout) {
			if (G.elements.winElements(locatorType, locator).size() == 0) {
				elementDisappeared = true;
				break;
			} else if (G.elements.winElements(locatorType, locator).size() > 0) {
				boolean elementDisplayed = true;
				try {
					elementDisplayed = G.elements.winElement(locatorType, locator).isDisplayed();
				} catch (StaleElementReferenceException e) {
					// Ignore staleElement exception
					logger.info("Ignoring stale element occurence", e);
				}

				if (!elementDisplayed) {
					elementDisappeared = true;
					break;
				} else {
					G.wait.sleep(2);
					totalTimeWaited = totalTimeWaited + 2;
				}
			}
		}

		if (!elementDisappeared) {
			throw new Exception(
					String.format("Element '%s' having locator '%s' is not invisible after waited for %s seconds",
							objectName, locator, timeout));
		} else {
			logger.info(String.format("Element '%s' is invisible", objectName));
		}
	}

	/*
	 * This method wait for the element to become stale. Please note that element
	 * should still be present in the DOM. Return false if element is not present in
	 * DOM / Element is not stale even after waiting for max timeout period
	 */
	public boolean waitForStalenessOfElement(WebElement element, String objectName, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.stalenessOf(element));
			return true;
		} catch (Exception e) {
			logger.error("Error while waiting for element to be stale: " + objectName + "Error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Wait for alert to be presented on the screen
	 * 
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public boolean waitForAlert(int timeout) throws Exception {
		try {
			WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.alertIsPresent());
			return true;
		} catch (Exception e) {
			logger.warn("No alert found even after waiting for " + timeout + " seconds");
			return false;
		}
	}
}
