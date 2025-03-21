package rpa.core.web;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;

public class Checkbox {

	private static Logger logger = LoggerFactory.getLogger(Checkbox.class);

	public void check(String objectXpath, String objectName) throws Exception {
		WebElement element = null;
		try {
			element = G.elements.object(objectXpath);
			check(element, objectName);
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}

	public void check(WebElement element, String objectName) throws Exception {
		try {
			if (element == null)
				throw new Exception("Checkbox not found: " + objectName);

			if (!element.isSelected()) {
				element.click();
				logger.info("Clicked on checkbox: " + objectName + ". Checkbox checked.");
			}
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}
	
	/**
	 * This method detects checkbox value with JS
	 * Provide the xpath till input tag
	 * @param xpath
	 * @param objectName
	 * @throws Exception
	 */
	public Boolean isCheckedJs(String xpath) {
		String str = StringUtils.EMPTY;
		try {
			String script = String.format(
					"return document.evaluate(\"%s\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.checked",
					xpath);
			Object obj = G.jse.executeScript(script);
			str = obj.toString();
		} catch (Exception e) {
			logger.error("Unable to detect the checkbox with JS.");
		}
		return Boolean.valueOf(str);
	}

	public void checkJs(String xpath, String objectName) throws Exception {
		try {
			if (G.wait.fluentForElement(xpath, objectName) != null) {
				if (Boolean.TRUE.equals(isCheckedJs(xpath))) {
					logger.info("checkBox {} is already checked", objectName);
				} else {
					check(xpath, objectName);
				}
			} else {
				throw new Exception(objectName + " checkbox not found.");
			}
		} catch (Exception e) {
			throw new Exception(String.format("Bishop Assistant is unable to check the %s checkbox", objectName));
		}
	}
	
	public void unCheckJs(String xpath, String objectName) throws Exception {
		try {
			if (G.wait.fluentForElement(xpath, objectName) != null) {
				if (Boolean.TRUE.equals(isCheckedJs(xpath))) {
					unCheck(xpath, objectName);
				} else {
					logger.info("checkBox {} is already unChecked", objectName);
				}
			} else {
				throw new Exception(objectName + " checkbox not found.");
			}
		} catch (Exception e) {
			throw new Exception(String.format("Bishop Assistant is unable to uncheck the %s checkbox", objectName));
		}
	}
	
	public void unCheck(String objectXpath, String objectName) throws Exception {
		WebElement element = null;
		try {
			element = G.elements.object(objectXpath);
			unCheck(element, objectName);
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}

	public void unCheck(WebElement element, String objectName) throws Exception {
		try {
			if (element == null)
				throw new Exception("Checkbox not found: " + objectName);

			if (element.isSelected()) {
				element.click();
				logger.info("Click on checkbox " + objectName + ". Checkbox unchecked.");
			}
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}

	public boolean isChecked(String objectXpath, String objectName) throws Exception {
		WebElement element = null;
		try {
			element = G.elements.object(objectXpath);
			if (element != null && element.isSelected()) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Cannot find " + objectName, e);
			throw new Exception("Cannot find " + objectName);
		}
		return false;
	}

	public void check(String locType, String locator, String objectName) throws Exception {
		WebElement element = null;
		try {
			element = G.elements.winElement(locType, locator);
			if (element != null && !element.isSelected()) {
				element.click();
				logger.info("Clicked on checkbox: " + objectName + ". Checkbox checked.");
			}
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}

	public void unCheck(String locType, String locator, String objectName) throws Exception {
		WebElement element = null;
		try {
			element = G.elements.winElement(locType, locator);
			if (element != null && element.isSelected()) {
				element.click();
				logger.info("Clicked on checkbox: " + objectName + ". Checkbox unchecked.");
			}
		} catch (Exception e) {
			logger.error("Error while clicking checkbox " + objectName, e);
			throw new Exception("Failed to check checkbox on " + objectName);
		}
	}

	/**
	 * This method checks/unchecks checkbox value
	 * 
	 * @param locatorType
	 * @param locator
	 * @param chkValue
	 * @throws Exception
	 */
	public void checkbox(String locatorType, String locator, boolean chkValue) throws Exception {
		try {
			WebElement ele = G.wait.fluentForElementHardWait(locatorType, locator, locator, 10);
			String chkValueStr = ele.getAttribute("LegacyDefaultAction");
			if ((StringUtils.equalsIgnoreCase(chkValueStr, "Check") && chkValue)
					|| (StringUtils.equalsIgnoreCase(chkValueStr, "Uncheck") && !chkValue)) {
				G.button.click(ele, locator);
			}
		} catch (Exception e) {
			logger.error("Error in setting value for checkbox : " + locator, e);
			throw new Exception("Error in setting value for checkbox : " + locator, e);
		}
	}

	/**
	 * This method checks/unchecks checkbox value
	 * 
	 * @param locatorType
	 * @param locator
	 * @param chkValue
	 * @throws Exception
	 */
	public void checkbox(WebElement ele, String objectName, boolean chkValue) throws Exception {
		try {
			String chkValueStr = ele.getAttribute("LegacyDefaultAction");
			if ((StringUtils.equalsIgnoreCase(chkValueStr, "Check") && chkValue)
					|| (StringUtils.equalsIgnoreCase(chkValueStr, "Uncheck") && !chkValue)) {
				G.button.clickWithoutView(ele, objectName);
			}
		} catch (Exception e) {
			logger.error("Error in setting value for checkbox : " + objectName, e);
			throw new Exception("Error in setting value for checkbox : " + objectName, e);
		}
	}

}
