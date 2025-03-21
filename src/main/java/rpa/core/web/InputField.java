package rpa.core.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.Browser;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopFrameworkException;
import rpa.core.file.PDFUtils;

public class InputField {

	private static Logger logger = LoggerFactory.getLogger(InputField.class);

	public void setTextWithoutClearing(String objectXpath, String objectName, String text) throws Exception {
		try {
			G.elements.object(objectXpath).sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Enters file path with local file detector
	 * 
	 * @param objectXpath - xpath of Webelement
	 * @param objectName  - Identifier for Webelement
	 * @param filePath    - AbsolutePath of file to upload
	 * @throws Exception
	 */
	public void setUploadFilePath(String objectXpath, String objectName, String filePath) throws Exception {
		try {
			logger.info("Using LocalFileDetector to set file path");
			WebElement element = G.elements.object(objectXpath);
			((RemoteWebElement) element).setFileDetector(new LocalFileDetector());
			element.sendKeys(filePath);
			logger.info(filePath + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to add filepath '%s' in field %s", filePath, objectName));
		}
	}

	public void clickAndClearText(String objectXpath, String objectName) throws Exception {
		G.button.click(objectXpath, objectName, false);
		G.wait.sleep(1);
		clearText(objectXpath, objectName, "");
	}

	public void clearText(String objectXpath, String objectName, String text) throws Exception {
		try {
			G.elements.object(objectXpath).clear();
			logger.info(objectName + " is cleared");
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to claer value in field %s", objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to clear value in field %s", objectName));
		}
	}

	public void clearTextUsingJs(String objectXpath, String objectName) throws Exception {
		try {
			G.jse.executeScript("arguments[0].value = '';", G.elements.object(objectXpath));
			logger.info(objectName + " is cleared");
		} catch (NullPointerException e) {
			throw new Exception(String.format("Unable to clear value in field %s", objectName));
		} catch (Exception e) {
			throw new Exception(String.format("Failed to clear value in field %s", objectName), e);
		}
	}

	/**
	 * Click on an input field and clear data using CTRL + A and BACKSPACE and enter
	 * text
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void clickClearSetTextUsingActions(String objectXpath, String objectName, String text) throws Exception {
		try {
			WebElement e = G.elements.object(objectXpath);
			new Actions(G.driver).click(e).keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
					.sendKeys(Keys.BACK_SPACE).click(e).sendKeys(Keys.END).keyDown(Keys.SHIFT).sendKeys(Keys.HOME)
					.keyUp(Keys.SHIFT).sendKeys(Keys.BACK_SPACE).sendKeys(text).build().perform();
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		}
	}

	public void clearText(String locType, String objectXpath, String objectName, String text) throws Exception {
		try {
			G.elements.winElement(locType, objectXpath).clear();
			logger.info(objectName + " is cleared");
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to clear value in field %s", objectName));
		}
	}

	public void actionPressKey(Keys keyStroke) {
		new Actions(G.driver).sendKeys(keyStroke).build().perform();
		logger.info("Pressed '" + keyStroke + "' using actions");
	}
	
	public void actionPressKey(String keyStroke) {
		new Actions(G.driver).sendKeys(keyStroke).build().perform();
		logger.info("Pressed '" + keyStroke + "' using actions");
	}

	public void setText(String objectXpath, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textbox = G.elements.object(objectXpath);
			textbox.clear();
			textbox.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (StaleElementReferenceException e) {
			WebElement textbox = G.elements.object(objectXpath);
			textbox.clear();
			textbox.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	public void setTextTab(String objectXpath, String objectName, String text) throws Exception {
		setText(objectXpath, objectName, text);
		G.elements.object(objectXpath).sendKeys(Keys.TAB);
	}

	/**
	 * Clears element text and enters input String with delay between Characters
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextwithDelay(String objectXpath, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textbox = G.elements.object(objectXpath);
			textbox.clear();
			for (String c : text.split("")) {
				textbox.sendKeys(c);
				G.wait.sleepforMilliseconds(75);
			}
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	public void setTextWithDelay(String locType, String locName, String objectName, String text)
			throws BishopFrameworkException {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textbox = G.elements.winElement(locType, locName);
			textbox.clear();
			for (String c : text.split("")) {
				textbox.sendKeys(c);
				G.wait.sleepforMilliseconds(75);
			}
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			throw new BishopFrameworkException(String.format("Failed to enter %s in field %s", text, objectName), e);
		}
	}

	/**
	 * For Windows desktop automation
	 * 
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setText(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textBox = G.elements.winElement(locType, locatorName);
			textBox.clear();
			textBox.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locType,
					locatorName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Enters text in passed element
	 * 
	 * @param element
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setText(WebElement element, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			element.clear();
			element.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Press key in passed element
	 * 
	 * @param element
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setText(WebElement element, String objectName, Keys key) throws Exception {
		try {
			element.sendKeys(key);
			logger.info(key + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", key.name().toString(), objectName));
		}
	}

	/**
	 * Clears, sets text and presses key in passed element using actions
	 * 
	 * @param element
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setAndPressKeyAction(String objectName, String text, Keys k) throws Exception {
		try {
			new Actions(G.driver).sendKeys(text).sendKeys(k).build().perform();
			logger.info(text + " entered in column " + objectName + " and pressed " + k.name().toString());
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Clicks, clears, sets text and presses key in passed element
	 * 
	 * @param element
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void clickSetAndPressKey(WebElement element, String objectName, String text, Keys k) throws Exception {
		try {
			element.click();
			setAndPressKey(element, objectName, text, k);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Clears, sets text and presses key in passed element
	 * 
	 * @param element
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setAndPressKey(WebElement element, String objectName, String text, Keys k) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			element.clear();
			if (text != null)
				element.sendKeys(text);
			element.sendKeys(k);
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	public void setTextWithoutClearing(String locType, String locatorName, String objectName, Keys keys)
			throws Exception {
		setTextWithoutClearing(locType, locatorName, objectName, "" + keys);
	}

	/**
	 * Enters text without clearing text in windows desktop element
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextWithoutClearing(String locType, String locatorName, String objectName, String text)
			throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textBox = G.elements.winElement(locType, locatorName);
			textBox.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * For Windows desktop automation. Set password
	 * 
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setPassword(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement textBox = G.elements.winElement(locType, locatorName);
			textBox.clear();
			textBox.sendKeys(text);
			logger.info("Password is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locType,
					locatorName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", "******", objectName));
		}
	}

	public void setTextEnter(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			WebElement e = G.elements.winElement(locType, locatorName);
			e.clear();
			e.sendKeys(text);
			e.sendKeys(Keys.ENTER);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locType,
					locatorName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * For Windows desktop automation
	 * 
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextTab(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			G.elements.winElement(locType, locatorName).clear();
			G.elements.winElement(locType, locatorName).sendKeys(text);
			G.elements.winElement(locType, locatorName).sendKeys(Keys.TAB);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locType,
					locatorName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * For Windows desktop automation
	 * 
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setPasswordTab(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			G.elements.winElement(locType, locatorName).clear();
			G.elements.winElement(locType, locatorName).sendKeys(text);
			G.elements.winElement(locType, locatorName).sendKeys(Keys.TAB);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Loc Type: %s || Loc Name: %s", objectName, locType,
					locatorName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", "", objectName));
		}
	}

	public void setText(String objectXpath, String objectName, Keys keyStroke) throws Exception {
		try {
			G.elements.object(objectXpath).sendKeys(keyStroke);
			logger.info(keyStroke.name() + " key stroke done in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", keyStroke.name(), objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", keyStroke.name(), objectName));
		}
	}

	public void setText(String locType, String objectXpath, String objectName, Keys keyStroke) throws Exception {
		try {
			G.elements.winElement(locType, objectXpath).sendKeys(keyStroke);
			logger.info(keyStroke.name() + " key stroke done in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", "****", objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", keyStroke.name(), objectName));
		}
	}

	public void check(String objectXpath, String objectName, String confirm) {
		try {
			String current = getText(objectXpath, objectName);
			if (confirm == "No")
				confirm = "false";
			else
				confirm = "true";
			if (confirm != current) {
				G.elements.object(objectXpath).click();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void setPassword(String objectXpath, String objectName, String password) throws Exception {
		try {
			WebElement e = G.elements.object(objectXpath);
			e.clear();
			e.sendKeys(password);
			logger.info("Password is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", "****", objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", "*******", objectName));
		}
	}

	public void setPasswordWithClear(String objectXpath, String objectName, String password) throws Exception {
		try {
			WebElement e = G.elements.object(objectXpath);
			e.sendKeys(password);
			logger.info("Password is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath: %s", objectName, objectXpath));
			throw new Exception(String.format("Unable to enter %s in field %s", "****", objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", "*******", objectName));
		}
	}

	public String getText(String objectXpath, String objectName) {
		return getText(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, true);
	}

	public String getText(String objectXpath, String objectName, int timeout, boolean errorLog) {
		return getText(objectXpath, objectName, timeout, errorLog, true);
	}

	public String getText(String objectXpath, String objectName, boolean errorLog, boolean textLog) {
		return getText(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, errorLog, textLog);
	}

	public String getText(String objectXpath, String objectName, boolean throwException) throws Exception {
		return getText(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, false, false, throwException);
	}

	public String getText(String objectXpath, String objectName, int timeout, boolean errorLog, boolean textLog) {
		try {
			return getText(objectXpath, objectName, timeout, errorLog, textLog, false);
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
	}

	public String getText(String objectXpath, String objectName, int timeout, boolean errorLog, boolean textLog,
			boolean throwException) throws Exception {
		try {
			String text = G.elements.object(objectXpath, timeout).getText();
			if (textLog)
				logger.info(objectName + " has value: " + text);
			return text;
		} catch (Exception e) {
			if (throwException) {
				logger.error("Error while getText from element: " + objectName, e);
				throw new Exception("Error while getText from element: " + objectName, e);
			} else if (errorLog)
				logger.error(String.format("Could not get text value for %s. Xpath: %s", objectName, objectXpath),
						e.getMessage());

			return StringUtils.EMPTY;
		}
	}

	/**
	 * only for windows desktop automation with timeout = 2 secs
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @return
	 */
	public String getTextW(String locType, String locatorName, String objectName) {
		return getText(locType, locatorName, objectName, 2, false, true);
	}

	/**
	 * only for windows desktop automation
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param timeout
	 * @param errorLog
	 * @param textLog
	 * @return
	 */
	public String getText(String locType, String locatorName, String objectName, int timeout, boolean errorLog,
			boolean textLog) {
		try {
			String text = G.elements.winElement(locType, locatorName).getText();
			if (textLog)
				logger.info(objectName + " has value: " + text);
			return text;
		} catch (Exception e) {
			if (errorLog)
				logger.error(String.format("Could not get text value for %s. Xpath: %s", objectName, locatorName),
						e.getMessage());
			return StringUtils.EMPTY;
		}
	}

	public List<String> getTexts(String objectXpath, String objectName) {
		return getTexts(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, true, true);
	}

	public List<String> getTexts(String objectXpath, String objectName, int timeout) {
		return getTexts(objectXpath, objectName, timeout, true, true);
	}

	public List<String> getTexts(String objectXpath, String objectName, int timeout, boolean errorLog,
			boolean textLog) {
		List<String> allTexts = new ArrayList<>();
		try {
			List<WebElement> eles = G.elements.objects(objectXpath, timeout);
			for (WebElement e : eles) {
				allTexts.add(e.getText());
			}
			if (textLog)
				logger.info(objectName + " has value: " + allTexts);
			return allTexts;
		} catch (Exception e) {
			if (errorLog)
				logger.error(String.format("Could not get text value for %s. Xpath: %s", objectName, objectXpath),
						e.getMessage());
			return allTexts;
		}
	}

	public List<String> getActualTexts(String objectXpath, String objectName) {
		return getActualTexts(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, true, true);
	}

	public List<String> getActualTexts(String objectXpath, String objectName, int timeout) {
		return getActualTexts(objectXpath, objectName, timeout, true, true);
	}

	public List<String> getActualTexts(String objectXpath, String objectName, int timeout, boolean errorLog,
			boolean textLog) {
		List<String> allTexts = new ArrayList<>();
		try {
			List<WebElement> eles = G.elements.objects(objectXpath, timeout);
			for (WebElement e : eles) {
				allTexts.add(e.getAttribute("textContent"));
			}
			if (textLog)
				logger.info(objectName + " has value: " + allTexts);
			return allTexts;
		} catch (Exception e) {
			if (errorLog)
				logger.error(String.format("Could not get text value for %s. Xpath: %s", objectName, objectXpath),
						e.getMessage());
			return allTexts;
		}
	}

	public String getInnerText(String objectXpath, String objectName) {
		return getText(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT, true);
	}

	public String getAttribute(String attribute, String objectXpath, String objectName) {
		return getAttribute(attribute, objectXpath, objectName, 2, false);
	}

	public String getAttribute(String attribute, String objectXpath, String objectName, boolean throwException)
			throws Exception {
		return getAttribute(attribute, objectXpath, objectName, 2, false, throwException);
	}

	public String getAttribute(String attribute, String objectXpath, String objectName, int timeout,
			boolean printLogs) {
		try {
			return getAttribute(attribute, objectXpath, objectName, timeout, printLogs, false);
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
	}

	public String getAttribute(String attribute, String objectXpath, String objectName, int timeout, boolean printLogs,
			boolean throwException) throws Exception {
		try {
			String text = G.elements.object(objectXpath, timeout).getAttribute(attribute);
			logger.info(objectName + " has value: " + text);
			return text;
		} catch (Exception e) {
			if (throwException) {
				logger.error("", e);
				throw new Exception(
						String.format("Error while getting attribute '%s' from element '%s'", attribute, objectName));
			} else if (printLogs) {
				logger.error("", e);
			}
			return StringUtils.EMPTY;
		}
	}

	public List<String> getAttributes(String attribute, String objectXpath, String objectName, int timeout) {
		return getAttributes(attribute, objectXpath, objectName, timeout, true, true);
	}

	public List<String> getAttributes(String attribute, String objectXpath, String objectName, int timeout,
			boolean errorLog, boolean textLog) {
		List<String> allTexts = new ArrayList<>();
		try {
			List<WebElement> elements = G.elements.objects(objectXpath, timeout);
			for (WebElement e : elements) {
				allTexts.add(e.getAttribute(attribute));
			}
			if (textLog)
				logger.info(objectName + " has value: " + allTexts);
			return allTexts;
		} catch (Exception e) {
			if (errorLog)
				logger.error(String.format("Could not get text value for %s. Xpath: %s", objectName, objectXpath),
						e.getMessage());
			return allTexts;
		}
	}

	/**
	 * Only for windows desktop automation
	 * 
	 * @param attribute
	 * @param locType
	 * @param objectXpath
	 * @param objectName
	 * @param timeout
	 * @param printLogs
	 * @return
	 */
	public String getAttribute(String attribute, String locType, String objectXpath, String objectName, int timeout,
			boolean printLogs) {
		try {
			String text = G.elements.winElement(locType, objectXpath).getAttribute(attribute);
			logger.info(objectName + " has value: " + text);
			return text;
		} catch (Exception e) {
			if (printLogs) {
				logger.error("", e);
			}
			return null;
		}
	}

	/**
	 * Gets attibute value from provided element
	 * 
	 * @param element
	 * @param attribute
	 * @param objectName
	 * @param printLogs
	 * @return
	 */
	public String getAttribute(WebElement element, String attribute, String objectName, boolean printLogs) {
		String text = null;
		try {
			if (element != null) {
				text = element.getAttribute(attribute);
				logger.info(String.format("Attribute %s for %s has value: %s", attribute, objectName, text));
			}
			return text;
		} catch (Exception e) {
			if (printLogs) {
				logger.error("", e);
			}
			return text;
		}
	}

	public void inputTextWithJs(String objectXpath, String objectName, String text) throws Exception {
		try {
			G.jse.executeScript("arguments[0].value='" + text.replace("'", "\\'") + "'",
					G.elements.object(objectXpath));
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	public void inputPwdWithJs(String objectXpath, String objectName, String password) throws Exception {
		try {
			G.jse.executeScript("arguments[0].value='" + password + "'", G.elements.object(objectXpath));
			logger.info("***** password is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", "*******", objectName));
		}
	}

	public String getTextWithJs(String objectXpath, String objectName, boolean printlog) {
		try {
			String text = (String) G.jse.executeScript("return jQuery(arguments[0]).text();",
					G.elements.object(objectXpath));
			if (printlog) {
				logger.info(objectName + " has value " + text);
			}
			return text;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public void setTextToListElements(String objectXpath, String objectName, Keys keyStroke) throws Exception {
		try {
			G.elements.objects(objectXpath).get(G.elements.objects(objectXpath).size() - 1).sendKeys(keyStroke);
			logger.info(keyStroke.name() + " key stroke done in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", keyStroke.name(), objectName));
		}
	}

	public void clickAndSetText(String objectXpath, String objectName, String text) throws Exception {
		try {
			G.button.click(objectXpath, objectName, false);
			G.wait.sleep(1);
			setText(objectXpath, objectName, text);
//			Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1).sendKeys(text);
//			Global.jse.executeScript("arguments[0].value='" + text + "'", Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1));
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter text %s in field %s", text, objectName));
		}
	}

	public void clickAndSetPassword(String objectXpath, String objectName, String text) throws Exception {
		try {
			G.button.click(objectXpath, objectName, false);
			G.wait.sleep(1);
			setPassword(objectXpath, objectName, text);
			logger.info("Password is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter text %s in field %s", "********", objectName));
		}
	}

	/**
	 * For windows desktop automation
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void clickAndSetText(String locType, String locatorName, String objectName, String text) throws Exception {
		try {
			G.button.click(locType, locatorName, objectName);
			G.wait.sleep(1);
			setTextEnter(locType, locatorName, objectName, text);
//			Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1).sendKeys(text);
//			Global.jse.executeScript("arguments[0].value='" + text + "'", Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1));
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter text %s in field %s", text, objectName));
		}
	}

	/**
	 * For windows desktop automation
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void clickAndSetTextTab(String locType, String locatorName, String objectName, String text)
			throws Exception {
		try {
			G.button.click(locType, locatorName, objectName);
			G.wait.sleep(1);
			setTextTab(locType, locatorName, objectName, text);
//      Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1).sendKeys(text);
//      Global.jse.executeScript("arguments[0].value='" + text + "'", Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1));
			logger.info(text + " text is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter text %s in field %s", text, objectName));
		}
	}

	/**
	 * This method enters text in child element this method could be used when we
	 * have found parent element of the element in which we have to set text
	 * 
	 * @param parentEle
	 * @param locatorType
	 * @param locatorNme
	 * @param objectName
	 * @param text
	 * @param tabPress
	 * @throws Exception
	 */
	public void enterTextChildEle(WebElement parentEle, String locatorType, String locatorNme, String objectName,
			String text, boolean tabPress) throws Exception {
		try {
			WebElement child = G.elements.innerEleOfParentEle(parentEle, locatorType, locatorNme);
			child.clear();
			child.sendKeys(text);
			if (tabPress)
				child.sendKeys(Keys.TAB);
			logger.info("Entered : " + text + " in : " + objectName);
			if (!child.getText().equalsIgnoreCase(text)) {
				logger.error("Text : " + text + " was entered in : " + objectName + " but not able to save");
				// throw new BishopRuleViolationException("Text : " + text + " was entered in : "
				// + objectName
				// + " but not able to save");
			}
		} catch (Exception e) {
			logger.error("Error in entering text : " + text + " in element : " + objectName, e);
			throw new Exception("Error in entering text : " + text + " in element : " + objectName, e);
		}
	}

	/**
	 * For windows desktop automation
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void clickAndSetPasswordTab(String locType, String locatorName, String objectName, String text)
			throws Exception {
		try {
			G.button.click(locType, locatorName, objectName);
			G.wait.sleep(1);
			setTextTab(locType, locatorName, objectName, text);
//      Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1).sendKeys(text);
//      Global.jse.executeScript("arguments[0].value='" + text + "'", Global.elements.objects(objectXpath).get(Global.elements.objects(objectXpath).size()-1));
			logger.info("Password is entered in field " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter text %s in field %s", "*****", objectName));
		}
	}

	/**
	 * Enter text and keys into text fields
	 * 
	 * @param element    - WebElement
	 * @param objectName
	 * @param text
	 * @param clear      - Clears the input filed if this true
	 * @throws Exception
	 */
	public void setText(WebElement element, String objectName, String text, boolean clear) throws Exception {
		try {
			text = StringUtils.trimToEmpty(text);
			if (clear)
				element.clear();
			element.sendKeys(text);
			logger.info(text + " text is entered in field " + objectName);
		} catch (NullPointerException e) {
			logger.error(String.format("Element %s not present. Xpath", objectName));
			throw new Exception(String.format("Unable to enter %s in field %s", text, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to enter %s in field %s", text, objectName));
		}
	}

	/**
	 * Enter data into input field if data is not blank
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextIfNotBlank(String locType, String locatorName, String objectName, String text) throws Exception {
		if (StringUtils.isNotBlank(text)) {
			setTextWithoutClearing(locType, locatorName, objectName, text);
		}
	}

	/**
	 * Enter data into input field if data is not blank
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextIfNotBlank(String objectXpath, String objectName, String text) throws Exception {
		if (StringUtils.isNotBlank(text)) {
			setText(objectXpath, objectName, text);
		}
	}

	/**
	 * Finds the child element of an element and set text
	 * 
	 * @param parentElement
	 * @param locatorType
	 * @param locator
	 * @param objectName
	 * @param text
	 * @throws Exception
	 */
	public void setTextInChildElement(WebElement parentElement, String locatorType, String locator, String objectName,
			String text) throws Exception {
		setTextInChildElement(parentElement, locatorType, locator, objectName, text, true);
	}

	/**
	 * Finds the child element of an element and set text
	 * 
	 * @param parentElement
	 * @param locatorType
	 * @param locator
	 * @param objectName
	 * @param text
	 * @param clear
	 * @throws Exception
	 */
	public void setTextInChildElement(WebElement parentElement, String locatorType, String locator, String objectName,
			String text, boolean clear) throws Exception {
		WebElement childElement = G.elements.innerEleOfParentEle(parentElement, locatorType, locator);
		setText(childElement, objectName, text, clear);
	}

	/**
	 * Gets text from child element. uses xpath as locator
	 * 
	 * @param parentXpath
	 * @param childXpath
	 * @param elementTitle
	 * @return
	 */
	public String getChildText(String parentXpath, String childXpath, String elementTitle) {
		return getChildText(Elements.XPATH, parentXpath, Elements.XPATH, childXpath, elementTitle);
	}

	/**
	 * Gets text from child element.
	 * 
	 * @param parentLocatorType
	 * @param parentLocator
	 * @param childLocatorType
	 * @param childLocator
	 * @param elementTitle
	 * @return
	 */
	public String getChildText(String parentLocatorType, String parentLocator, String childLocatorType,
			String childLocator, String elementTitle) {
		String text = StringUtils.EMPTY;
		WebElement parent = G.elements.winElement(parentLocatorType, parentLocator);
		WebElement child = G.elements.getInnerElement(parent, childLocatorType, childLocator, true);
		text = child.getText();
		logger.info(String.format("Text found in %s - %s", elementTitle, text));
		return text;
	}

	/**
	 * Gets text from child element from provided element. uses xpath as locator
	 * 
	 * @param parent
	 * @param childXpath
	 * @param elementTitle
	 * @return
	 */
	public String getChildText(WebElement parent, String childXpath, String elementTitle) {
		return getChildText(parent, Elements.XPATH, childXpath, elementTitle);
	}

	/**
	 * Gets text from child element from provided element.
	 * 
	 * @param parent
	 * @param childLocatorType
	 * @param childLocator
	 * @param elementTitle
	 * @return
	 */
	public String getChildText(WebElement parent, String childLocatorType, String childLocator, String elementTitle) {
		String text = StringUtils.EMPTY;
		WebElement child = G.elements.getInnerElement(parent, childLocatorType, childLocator, true);
		text = child.getText();
		logger.info(String.format("Text found in %s - %s", elementTitle, text));
		return text;
	}
}
