package rpa.core.web;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.Browser;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.steps.SetupHooks;

public class Window {
	private static Logger logger = LoggerFactory.getLogger(Window.class);
	private static String parentWinHandle = null;
	private static String previousWinHandle = null;

	public void printToPDF() {
		G.jse.executeScript("window.print();");
	}

	public String getAlertTextAndAccept() {
		String alertText = "";
		try {
			Alert alert = G.driver.switchTo().alert();
			if (alert != null) {
				alertText = alert.getText();
				alert.accept();
			}
		} catch (NoAlertPresentException | NoSuchWindowException e) {
		} catch (Exception e) {
			logger.error("Failed to dismiss alert", e);
		}
		return alertText;
	}

	public String getAlertTextAndDismiss() {
		String alertText = "";
		if (G.windowsApp)
			return alertText;
		try {
			Alert alert = G.driver.switchTo().alert();
			if (alert != null) {
				alertText = alert.getText();
				alert.dismiss();
			}
		} catch (NoAlertPresentException | NoSuchWindowException e) {
		} catch (Exception e) {
			logger.error("Failed to dismiss alert", e);
		}
		return alertText;
	}

	public void scrollElementIntoView(String xpath) {
		scrollElementIntoView(G.elements.object(xpath));
		Screenshot.take();
	}

	public void scrollElementIntoView(WebElement element) {
		G.jse.executeScript(
				"arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"center\"});", element);
		G.wait.sleep(1);
	}

	public void scrollElementIntoViewQuick(String xpath, String objectName) {
		G.jse.executeScript("arguments[0].scrollIntoView();", G.elements.object(xpath));
		logger.info("Successfully scrolled to '{}'", objectName);
	}

	public void scrollElementIntoView(String xpath, boolean screenshot) {
		scrollElementIntoView(G.elements.object(xpath));
		if (screenshot)
			Screenshot.take();
	}

	/**
	 * Get total scroll height in no. of pixel
	 * 
	 * @param divLocatorType
	 * @param divLocator
	 * @return
	 */
	public long getScrollHeight(String divLocatorType, String divLocator) {
		JavascriptExecutor jsExec = (JavascriptExecutor) G.driver;
		return (long) jsExec.executeScript("return arguments[0].scrollHeight",
				G.elements.winElement(divLocatorType, divLocator));
	}

	public void scrollDivVertically(String divLocatorType, String divLocator, int noOfPixelToScroll) {
		JavascriptExecutor jsExec = (JavascriptExecutor) G.driver;
		jsExec.executeScript("arguments[0].scrollTop += " + noOfPixelToScroll,
				G.elements.winElement(divLocatorType, divLocator));
		G.wait.sleep(1);
	}

	public void scrollDivHorizontally(String divLocatorType, String divLocator, int noOfPixelToScroll) {
		JavascriptExecutor jsExec = (JavascriptExecutor) G.driver;
		jsExec.executeScript("arguments[0].scrollLeft += " + noOfPixelToScroll,
				G.elements.winElement(divLocatorType, divLocator));
		G.wait.sleep(1);
	}

	public void implicitWait(int secs) {
		G.driver.manage().timeouts().implicitlyWait(secs, TimeUnit.SECONDS);
	}

	public void implicitWaitDefault() {
		G.driver.manage().timeouts().implicitlyWait(Browser.DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS);
	}

	public void openNewTab(String url) {
		G.jse.executeScript("window.open('" + url + "', '__blank__');");
	}

	public boolean pageContains(String expText) {
		return StringUtils.contains(G.driver.getPageSource(), expText);
	}

	public boolean switchToWindowWithTitle(String title, int retry) {
		boolean switchSuccess = false;
		String parentTitle = "";
		int retries = 0;
		String parentWindow = StringUtils.EMPTY;
		try {
			parentWindow = G.driver.getWindowHandle();
		} catch (NoSuchWindowException nw) {
		}

		do {
			parentTitle = switchToWindowWithTitle(title);
			if (!StringUtils.equals(parentTitle, G.driver.getTitle())
					&& StringUtils.equals(title, G.driver.getTitle())) {
				switchSuccess = true;
				break;
			} else
				G.wait.sleep(1);
			retries++;
		} while (!switchSuccess && retries <= retry);

		if (!switchSuccess) {
			G.driver.switchTo().window(parentWindow);
			parentWinHandle = previousWinHandle = StringUtils.EMPTY;
			logger.warn("Switch to window {} unsuccessful after {} attempts", title, retry);
		}
		return switchSuccess;
	}

	public String switchToWindowWithTitle(String title) {
		String parentWindowTitle = StringUtils.EMPTY;
		try {
			parentWinHandle = G.driver.getWindowHandle();
			previousWinHandle = G.driver.getWindowHandle();
			parentWindowTitle = G.driver.getTitle();
		} catch (NoSuchWindowException nw) {

		}
		for (String winHandle : G.driver.getWindowHandles()) {
			G.driver.switchTo().window(winHandle);
			if (StringUtils.equals(G.driver.getTitle(), title)) {
				logger.info("Switched to window  with title " + title);
				Screenshot.take();
				break;
			}
		}
		return parentWindowTitle;
	}

	/**
	 * Finds a frame containing objectXpath anywhere in page using DFS & switches to
	 * that frame
	 * 
	 * @param objectXpath
	 * @return true if found any frame else false
	 * @throws IOException
	 */
	public boolean switchToFrameHavingChildXpath(String objectXpath) throws IOException {
//		G.window.switchToDefaultContent();
//		String js = FirestoreDB.getResourceAsString("js/iframes.js");
//		G.jse.executeScript(js);
//		objectXpath = objectXpath.replace("\"", "\\\"");
//		List<Long> indexes = (List<Long>) G.jse
//				.executeScript("return getIndexOfIframesWithXpath(\"" + objectXpath + "\")");
//		if (indexes == null) {
//			return false;
//		}
//		for (int i = 0; i < indexes.size(); i++) {
//			Long index = indexes.get(i);
//			G.window.switchToFrameUsingXpath(
//					"(//*[name()='frame' or name()='iframe' or local-name()='frame' or local-name()='iframe'])[" + index
//							+ "]");
//		}
//		return true;
		return false;
	}

	public boolean switchToWindowWithTitleContains(String title, int retry) throws Exception {
		String originalWindow = StringUtils.EMPTY;
		try {
			parentWinHandle = G.driver.getWindowHandle();
			previousWinHandle = G.driver.getWindowHandle();
			originalWindow = G.driver.getWindowHandle();
		} catch (NoSuchWindowException nw) {
		}

		boolean switchSuccess = false;
		int retries = 0;
		do {
			G.wait.sleep(1);
			try {
				switchToWindowWithTitleContains(title);
				if (StringUtils.containsIgnoreCase(G.driver.getTitle(), title))
					switchSuccess = true;
			} catch (Exception e) {
				// do nothing
			}
			retries++;
		} while (!switchSuccess && retries <= retry);
		if (!switchSuccess) {
			if (StringUtils.isNotBlank(originalWindow)) {
				G.driver.switchTo().window(originalWindow);
			}
			logger.warn("Switch to window {} unsuccessful after {} attempts", title, retry);
			if (StringUtils.isNotBlank(originalWindow))
				G.driver.switchTo().window(originalWindow);
		}
		return switchSuccess;
	}

	public void switchToWindowWithTitleContainsWindows(String windowTitle, int timeout) throws Exception {
		int timeWaited = 0;
		boolean windowFound = false;
		do {
			timeWaited += 1;
			Thread.sleep(1000);
			try {
				for (String session : G.driver.getWindowHandles()) {
					try {
						G.driver.switchTo().window(session);
						if (StringUtils.containsIgnoreCase(G.driver.getTitle(), windowTitle)) {
							windowFound = true;
							logger.info("Switched to window with title : " + G.driver.getTitle());
							break;
						}
					} catch (Exception e) {
						// in case if window closes and is also picked in getWindowHandles()
						logger.info("Window not found retrying..");
					}
				}
				if (windowFound == true) {
					logger.info("Switched to window " + windowTitle + " successfully");
					break;
				}
			} catch (WebDriverException we) {
				logger.error("Error in switching to window : " + windowTitle, we);
				logger.info("Retrying switching to window : " + windowTitle);
			} catch (Exception e) {
				logger.error("Error in switching to window : " + windowTitle, e);
				throw new Exception("Error in switching to window : " + windowTitle, e);
			}
		} while (timeWaited < timeout);
		Screenshot.take();
	}

	public String switchToWindowWithTitleContains(String title) throws Exception {
		String parentWindowTitle = StringUtils.EMPTY;
		try {
			parentWinHandle = G.driver.getWindowHandle();
			previousWinHandle = G.driver.getWindowHandle();
			parentWindowTitle = G.driver.getTitle();
		} catch (NoSuchWindowException e) {
			logger.warn("Current window is already closed");
		}
		for (String winHandle : G.driver.getWindowHandles()) {
			G.driver.switchTo().window(winHandle);
			try {
				if (StringUtils.containsIgnoreCase(G.driver.getTitle(), title)) {
					logger.info("Switched to window  with title " + title);
					break;
				}
			} catch (NoSuchWindowException nw) {

			}
		}
		if (StringUtils.equals(parentWindowTitle, G.driver.getTitle())) {
			logger.error("Window with title {} not found.", title);
//			throw new Exception(String.format("Window %s did not open.", title));
		}
		return parentWindowTitle;
	}

	public void switchToWindowWithURL(String urlptt) {
		previousWinHandle = G.driver.getWindowHandle();
		for (String winHandle : G.driver.getWindowHandles()) {
			G.driver.switchTo().window(winHandle);
			G.wait.forPageToLoad();
			String curUrl = G.driver.getCurrentUrl();
			logger.info("Switched to window  with url pattern " + curUrl);
			if (curUrl.equals(urlptt)) {
				logger.info("Switched to window  with url pattern " + urlptt);
				Screenshot.take();
				break;
			}
		}
	}

	public void switchToChild() {
		parentWinHandle = G.driver.getWindowHandle();
		for (String winHandle : G.driver.getWindowHandles()) {
			if (!StringUtils.equals(winHandle, parentWinHandle)) {
				G.driver.switchTo().window(winHandle);
				logger.info("Switched to window " + G.driver.getTitle());
				Screenshot.take();
				break;
			}
		}
	}

	/**
	 * Switches to tab other than the provided one. Useful for scenario where title
	 * of tab is unknown and only 2 tabs present.
	 * 
	 * @param mainWindow
	 */
	public void switchToOtherWindow(String mainWindow) {
		Set<String> windows = G.driver.getWindowHandles();
		if (CollectionUtils.size(windows) > 1) {
			for (String window : windows) {
				if (!StringUtils.equals(window, mainWindow)) {
					G.driver.switchTo().window(window);
					logger.info(String.format("Switched to window with title %s", G.driver.getTitle()));
					Screenshot.take();
					break;
				}
			}
		} else {
			logger.warn("No other window present to switch to.");
		}
	}

	public void switchToFrameUsingXpath(String xpath) {
		G.driver.switchTo().frame(G.elements.object(xpath));
		logger.debug("Switched to frame " + xpath);
	}

	public void switchToFrameUsingXpath(String xpath, int waitTimeInSecond) {
		switchToFrame(G.elements.object(xpath), waitTimeInSecond);
	}

	public void switchToDefaultContent() {
		G.driver.switchTo().defaultContent();
		logger.debug("Switched to default content");
	}

	public void switchToFrame(WebElement frameElement) {
		G.driver.switchTo().frame(frameElement);
		logger.debug("Switched to frame " + frameElement);
	}

	public void switchToFrame(String nameOrId) {
		G.driver.switchTo().frame(nameOrId);
		logger.debug("Switched to frame " + nameOrId);
	}

	public void switchToFrame(WebElement frameElement, int waitTimeInSecond) {
		WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(waitTimeInSecond));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
		logger.debug("Switched to frame " + frameElement);
	}

	public void switchToFrame(String frameNameOrId, int waitTimeInSecond) {
		WebDriverWait wait = new WebDriverWait(G.driver, Duration.ofSeconds(waitTimeInSecond));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameNameOrId));
		logger.debug("Switched to frame " + frameNameOrId);
	}

	/**
	 * Switch to nested frames
	 * 
	 * @param frameNames separated by comma Example - frame1,frame2
	 */
	public void switchToFrames(String frameNames) {
		G.window.switchToDefaultContent();
		String[] frames = frameNames.split(",");
		for (String frameName : frames) {
			String oFrame = String.format("//frame[@name='%s']", frameName);
			G.window.switchToFrame(G.elements.object(oFrame));
		}
	}

	public void switchToParent() {
		if (parentWinHandle != null) {
			G.driver.switchTo().window(parentWinHandle);
			logger.debug("Switched to window " + G.driver.getTitle());
			Screenshot.take();
			parentWinHandle = null;
		} else {
			logger.warn("Driver is pointing to parent window.");
		}
	}

	public void closeChildAndSwitchToParent() {
		if (StringUtils.isNotBlank(parentWinHandle)) {
			String currentWindowHandle = StringUtils.EMPTY;
			try {
				currentWindowHandle = G.driver.getWindowHandle();
			} catch (NoSuchWindowException e) {
				logger.info("Child window is already closed");
			}
			if (StringUtils.isNotBlank(currentWindowHandle)) {
				if (!currentWindowHandle.equals(parentWinHandle)) {
					G.driver.close();
					switchToParent();
				} else {
					logger.warn("Driver is pointing to parent window.");
				}
			} else {
				switchToParent();
			}
		} else {
			logger.warn("Driver is pointing to parent window.");
		}
	}

	/**
	 * Closes all tabs except for the current tab
	 * 
	 * @param windowHandle
	 */
	public void closeAllExcept() {
		closeAllTabsExcept(G.driver.getWindowHandle());
	}

	/**
	 * Closes all tabs except for the tab passed
	 * 
	 * @param windowHandle
	 */
	public void closeAllTabsExcept(String windowHandle) {
		Set<String> windows = G.driver.getWindowHandles();
		for (String win : windows) {
			if (!windowHandle.equals(win)) {
				G.driver.switchTo().window(win);
				G.driver.close();
			}
		}
		G.driver.switchTo().window(windowHandle);
		logger.info("Driver is pointing to window " + G.driver.getTitle());
	}

	public void scrollWithJs(int x, int y) {
		G.jse.executeScript("window.scrollBy(" + x + "," + y + ")");
		logger.debug("Scrolled to element at x- " + x + " and y-" + y);
	}

	public void navigate(String url) {
		logger.info("Navigated to url: " + url);
		G.driver.navigate().to(url);
		Screenshot.take();
	}

	public void switchToPreviousWindow() {
		for (String winHandle : G.driver.getWindowHandles()) {
			if (StringUtils.equals(winHandle, previousWinHandle)) {
				G.driver.switchTo().window(winHandle);
				previousWinHandle = winHandle;
				logger.info("Switched to window " + winHandle);
				Screenshot.take();
				break;
			}
		}
	}

	public String getAlertText() {
		try {
			String text = G.driver.switchTo().alert().getText();
			return text;
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
	}

	public boolean isAlertPresent() {
		try {
			G.driver.switchTo().alert();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void goBack() {
		G.driver.navigate().back();
	}

	public String getCurrentUrl() {
		return G.driver.getCurrentUrl();
	}

	/**
	 * Scroll pages up and down 3 times. Helps to load page
	 */
	public static void scrollPageUpDown() {
		scrollPageUpDown(3);
	}

	/**
	 * Scroll pages up
	 */
	public static void pageUp() {
		G.jse.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
		G.wait.sleep(1);
	}

	/**
	 * Scroll pages down
	 */
	public static void pageDown() {
		G.jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		G.wait.sleep(1);
	}

	/**
	 * Scroll pages up and down @times times. Helps to load page
	 */
	public static void scrollPageUpDown(int times) {
		for (int loop = 1; loop <= times; loop++) {
			pageDown();
			pageUp();
		}
	}

	/**
	 * Maximize browser/windows application window
	 */
	public void maximizeBrowser() {
		try {
			G.driver.manage().window().maximize();
			logger.debug("Window maximised successfully");
		} catch (Exception e) {
			logger.error("Error in maximising window ");
		}
	}

	public void refreshBrowser() {
		G.driver.navigate().refresh();
	}

	public String manageDialog(String dialogName, boolean dialogInSeperateWindow, String actionOnDialog)
			throws Exception {
		return manageDialog(dialogName, dialogInSeperateWindow, false, actionOnDialog);
	}

	/**
	 * Manage windows alert and close them if required or click on any button
	 * present within popup
	 * 
	 * @param dialogName
	 * @param dialogInSeperateWindow
	 * @param closeDialog
	 * @return alertText as String
	 * @throws Exception
	 */
	public String manageDialog(String dialogName, boolean dialogInSeperateWindow, boolean closeDialog)
			throws Exception {
		return manageDialog(dialogName, dialogInSeperateWindow, closeDialog, "");
	}

	public String manageDialog(String dialogName, boolean dialogInSeperateWindow, boolean closeDialog,
			int windowCountWithoutAlert) throws Exception {
		return manageDialog(dialogName, dialogInSeperateWindow, closeDialog, "", windowCountWithoutAlert);
	}

	public String manageDialog(String dialogName, boolean dialogInSeperateWindow, boolean closeDialog,
			String actionOnDialog) throws Exception {
		return manageDialog(dialogName, dialogInSeperateWindow, closeDialog, actionOnDialog, 1);
	}

	/**
	 * Manage windows alert and close them if required or click on any button
	 * present within popup
	 * 
	 * @param dialogName
	 * @param dialogInSeperateWindow
	 * @param closeDialog
	 * @return alertText as String
	 * @throws Exception
	 */
	public String manageDialog(String dialogName, boolean dialogInSeperateWindow, boolean closeDialog,
			String actionOnDialog, int windowCountWithoutAlert) throws Exception {
		String oTextInDialog = StringUtils.EMPTY;
		String oBtnClose = "//Button[@Name='Close']";
		String text = StringUtils.EMPTY;
		boolean isDialogPresent = false;
		String parentWindowTitle = StringUtils.EMPTY;
		try {
			if (dialogInSeperateWindow) {
				oTextInDialog = String.format(
						"//*[@Name='%s' and (@LocalizedControlType='window' or @LocalizedControlType='dialog')]/Text",
						dialogName);
				if (G.driver.getWindowHandles().size() > windowCountWithoutAlert) {
					parentWindowTitle = G.driver.getTitle();
					if (G.window.switchToWindowWithTitle(dialogName, 1)) {
						isDialogPresent = true;
					}
				}
			} else {
				oTextInDialog = String.format("//*[@Name='%s' and @LocalizedControlType='dialog']/Text", dialogName);
				if (G.wait.fluentForElementHardWait(Elements.NAME, dialogName, "Close app alert", 1) != null) {
					isDialogPresent = true;
				}
			}

			if (isDialogPresent) {
				text = G.inputfield.getAttribute("Name", oTextInDialog, "Text in dialog");
				if (closeDialog) {
					G.button.click(Elements.XPATH, oBtnClose, "Close window", false);
					G.wait.sleep(1);
				} else {
					if (StringUtils.isNotBlank(actionOnDialog)) {
						G.button.click(Elements.NAME, actionOnDialog, actionOnDialog + " action on alert", false);
						G.wait.sleep(1);
					}
				}
			} else {
				logger.info("No dialog present");
			}
		} catch (Exception e) {
			logger.error("Error while handling popup: " + dialogName, e);
			if (G.wait.fluentForElement(oBtnClose, "Close window", 1) != null) {
				G.button.click(Elements.XPATH, oBtnClose, "Close window");
			}
			throw new Exception("Error while handling popup: " + dialogName);
		} finally {
			if (isDialogPresent && dialogInSeperateWindow) {
				G.window.switchToWindowWithTitleContains(parentWindowTitle, 5);
			}
		}
		return text;
	}

	/**
	 * Switch to a window containing @urlptt in url
	 * 
	 * @param urlptt
	 */
	public void switchToWindowWithURLContains(String urlptt) {
		previousWinHandle = G.driver.getWindowHandle();
		for (String winHandle : G.driver.getWindowHandles()) {
			G.driver.switchTo().window(winHandle);
			G.wait.forPageToLoad();
			String curUrl = G.driver.getCurrentUrl();
			if (curUrl.contains(urlptt)) {
				logger.info("Switched to window  with url pattern " + urlptt);
				Screenshot.take();
				break;
			}
		}
	}

	/**
	 * This method closes particular index window element for eg, we want to close
	 * window having index 2. this method will close and window and later switch to
	 * window added as param - baseWindow
	 * 
	 * @param index
	 * @param baseWindow
	 * @return
	 * @throws Exception
	 */
	public boolean closeWindowWithIndex(int index, String baseWindow) throws Exception {
		boolean closeFlag = false;
		List<String> handles = new ArrayList<String>(G.driver.getWindowHandles());
		try {
			G.driver.switchTo().window(handles.get(3));
			G.driver.close();
			closeFlag = true;
		} catch (Exception e) {
			logger.error("Error in closing window with index : " + index, e);
		} finally {
			switchToWindowWithTitleContains(baseWindow, 5, false);
		}
		return closeFlag;
	}

	/**
	 * Switching to window
	 * 
	 * @param windowTitle
	 * @param timeout
	 * @param maximize
	 * @throws Exception
	 */
	public void switchToWindowWithTitleContains(String windowTitle, int timeout, boolean maximize) throws Exception {
		int timeWaited = 0;
		boolean windowFound = false;
		do {
			timeWaited += 1;
			Thread.sleep(1000);
			try {
				for (String session : G.driver.getWindowHandles()) {
					try {
						G.driver.switchTo().window(session);
						if (StringUtils.containsIgnoreCase(G.driver.getTitle(), windowTitle)) {
							windowFound = true;
							logger.info("Switched to window with title : " + G.driver.getTitle());
							break;
						}
					} catch (Exception e) {
						// in case if window closes and is also picked in getWindowHandles()
						logger.info("Window not found retrying..");
					}
				}
				if (windowFound) {
					logger.info("Switched to window " + windowTitle + " successfully");
					break;
				}
			} catch (WebDriverException we) {
				logger.info("Retrying switching to window : " + windowTitle, we);
			} catch (Exception e) {
				logger.error("Error in switching to window : " + windowTitle, e);
				throw new Exception("Error in switching to window : " + windowTitle, e);
			}
		} while (timeWaited < timeout);
		Screenshot.take();
		if (windowFound && maximize)
			G.window.maximizeBrowser();
		if (!windowFound) {
			logger.error("Window " + windowTitle + " wasn't able to open in : " + timeout + " secs");
			throw new BishopRuleViolationException(
					"Window " + windowTitle + " wasn't able to open in : " + timeout + " secs");
		}
	}

	/**
	 * This method waits until specified time for element to be not null by
	 * refreshing the page
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param waitTime
	 * @return
	 * @throws Exception
	 */
	public boolean refreshWaitForElement(String objectXpath, String objectName, int waitTime) throws Exception {
		int cnt = 0;
		waitTime = (int) Math.ceil(waitTime / 3);
		try {
			do {
				if (G.elements.object(objectXpath) == null) {
					G.window.refreshBrowser();
					G.wait.forPageToLoad();
					G.wait.sleep(3);
					cnt++;
				} else {
					return true;
				}
			} while (cnt <= waitTime);
		} catch (Exception e) {
			logger.error("Error in waiting for : " + objectName, e);
			throw new Exception("Error in waiting for : " + objectName, e);
		}
		return false;
	}

	/**
	 * Exit screen by Alt + F4 To be used for Windows Automation instead of clicking
	 * X button on the window
	 */
	public void exitAltF4() {
		exitAltF4(null);
	}

	public void exitAltF4(WebElement e) {
		Actions actions = new Actions(G.driver);
		if (e != null)
			actions.moveToElement(e).click();
		actions.keyDown(Keys.ALT).sendKeys(Keys.F4).keyUp(Keys.ALT).build().perform();
		G.wait.sleep(1);
		logger.info("Pressed Alt + F4");
	}

	/**
	 * Save using Ctrl + S To be used for Windows Automation instead of clicking X
	 * button on the window
	 */
	public void saveCtrlS() {
		saveCtrlS(null);
	}

	public void saveCtrlS(WebElement e) {
		Actions actions = new Actions(G.driver);
		if (e != null)
			actions.moveToElement(e).click();
		actions.keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL).build().perform();
		logger.info("Pressed Ctrl + S");
	}

	/**
	 * Presses combonation of keys defined by Keys.chord(); Usage:
	 * Global.window.pressCombo(Keys.chord(Keys.CONTROL, "f"));
	 * 
	 * @param chord
	 */
	public void pressCombo(String chord) {
		try {
			new Actions(G.driver).sendKeys(chord).build().perform();
			logger.info(String.format("Pressed combination %s", chord));
		} catch (Exception e) {
			logger.error(String.format("Unable to press combination %s", chord), e);
			throw e;
		} finally {
			Screenshot.take();
		}
	}

	/**
	 * Focuses on the active window again by switching to the current window
	 */
	public void focusWindow() {
		String handle = G.driver.getWindowHandle();
		G.driver.switchTo().window(handle);
	}

	/**
	 * Switches frames according to sequence of arguments
	 * 
	 * @param frames - xpaths of frames
	 */
	public void switchToFrame(String... frames) {
		G.window.switchToDefaultContent();
		for (String frame : frames) {
			G.wait.fluentForElement(frame, "Frame");
			G.window.switchToFrame(G.elements.object(frame));
			G.wait.sleep(1);
		}
	}

	public String getTitle() {
		return G.driver.getTitle();
	}

	public boolean isClosed() {
		try {
			G.driver.getTitle();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean checkWindowExists_Win(String windowName, String locatorType, int timeout) {
		try {
			SetupHooks.launchWindowsDriverWithRoot(windowName, false, timeout, locatorType);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
