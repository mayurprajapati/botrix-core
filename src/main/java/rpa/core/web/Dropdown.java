package rpa.core.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.Browser;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopException;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.ParseUtils;

public class Dropdown {

	private static Logger logger = LoggerFactory.getLogger(Dropdown.class);

	public void selectByIndex(String objectXpath, String objectName, int index) throws Exception {
		Select select = null;
		try {
			select = new Select(G.elements.object(objectXpath));
			select.selectByIndex(index);
			logger.info("Selected element " + index + " of dropdown " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to select option %s from dropdown", index, objectName));
		}
	}

	public void selectByValue(String objectXpath, String objectName, String value) throws Exception {
		Select select = null;
		try {
			select = new Select(G.elements.object(objectXpath));
			select.selectByValue(value);
			logger.info("Selected " + value + " of the dropdown " + objectName);
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to select %s value from dropdown", value, objectName));
		}
	}

	public void selectDropdown(String objectXpath, String objectName, String visibleText) throws Exception {
		selectStatus(objectXpath, objectName, visibleText);
	}

	public void selectStatus(String objectXpath, String objectName, String visibleText) throws Exception {
		selectStatus(objectXpath, objectName, visibleText, StringUtils.EMPTY);
	}

	public void selectStatus(String objectXpath, String objectName, String visibleText, String customMappingField)
			throws Exception {
		try {
			if (StringUtils.isNotBlank(customMappingField))
				throw new Exception("Using custom mapping field " + customMappingField);
			selectByVisibleTextIgnoreCase(objectXpath, objectName, visibleText);
		} catch (Exception e) {
			Map<String, String> statusMapping = null;
			logger.info(String.format("%s status not found in dropdown %s. Finding mapping from flow > featuer toggle.",
					visibleText, objectName));
			if (StringUtils.isBlank(customMappingField)) {
				statusMapping = G.executionMetrics.getFlow().getFeatureToggle().getStatus();
				logger.info(String.format("Using custom mapping from field status. Mapping %s", statusMapping));
			} else {
				statusMapping = G.executionMetrics.getFlow().getFeatureToggle().getCustomMap(customMappingField);
				logger.info(String.format("Using custom mapping from field %s. Mapping %s", customMappingField,
						statusMapping));
			}

			if (MapUtils.isNotEmpty(statusMapping)) {
				logger.info("Mapping -> " + statusMapping);
				String toStatus = null;
				for (String fromStatus : statusMapping.keySet()) {
					if (StringUtils.equalsIgnoreCase(
							StringUtils.trimToEmpty(fromStatus).replaceAll("[^a-zA-Z0-9]", StringUtils.EMPTY),
							StringUtils.trimToEmpty(visibleText).replaceAll("[^a-zA-Z0-9]", StringUtils.EMPTY))) {
						toStatus = statusMapping.get(fromStatus);
						break;
					}
				}
				if (toStatus != null) {
					selectByVisibleTextIgnoreCase(objectXpath, objectName, toStatus);
				} else {
					logger.error(String.format("%s status not found in dropdown %s and no custom mapping found.",
							visibleText, objectName));
					throw new BishopException(
							String.format("%s status not found in dropdown %s and no custom mapping found.",
									visibleText, objectName));
				}
			} else {
				logger.error(String.format("%s status not found in dropdown %s and no custom mapping found.",
						visibleText, objectName));
				throw new BishopException(String.format(
						"%s status not found in dropdown %s and no custom mapping found.", visibleText, objectName));
			}
		}
	}

	public void selectByVisibleTextIgnoreCase(String objectXpath, String objectName, String visibleText)
			throws Exception {
		selectByVisibleTextIgnoreCase(objectXpath, objectName, new String[] { visibleText });
	}

	public String getDropDownAcutalValue(String objectXpath, String objectName, String partialValue) throws Exception {
		List<String> allValues = getAllValues(objectXpath, objectName);
		String actualVisibleText = null;
		for (String value : allValues) {
			if (StringUtils.equalsIgnoreCase(ParseUtils.keepAlphabetsAndNumbers(value),
					ParseUtils.keepAlphabetsAndNumbers(partialValue))) {
				actualVisibleText = value;
			}
		}
		if (actualVisibleText == null) {
			logger.error(String.format("No value matching '%s' found for dropdown %s", partialValue, objectName));
			throw new BishopException(
					String.format("No value matching '%s' found for dropdown %s", partialValue, objectName));
		}
		return actualVisibleText;
	}

	public void selectByVisibleTextIgnoreCase(String objectXpath, String objectName, String... optionsToCheck)
			throws Exception {
		List<String> allValues = getAllValues(objectXpath, objectName);
		String actualVisibleText = null;
		outerloop: for (String value : allValues) {
			for (String option : optionsToCheck) {
				if (StringUtils.equalsIgnoreCase(ParseUtils.keepAlphabetsAndNumbers(value),
						ParseUtils.keepAlphabetsAndNumbers(option))) {
					actualVisibleText = value;
					break outerloop;
				}
			}
		}
		if (actualVisibleText != null) {
			selectByVisibleText(objectXpath, objectName, actualVisibleText);
		} else {
			logger.error(String.format(Arrays.deepToString(optionsToCheck) + " options not found for dropdown %s",
					objectName));
			throw new BishopException(String
					.format(Arrays.deepToString(optionsToCheck) + " options not found for dropdown %s", objectName));
		}
	}

	public void selectByVisibleTextContainsCase(String objectXpath, String objectName, String optionsToCheck)
			throws Exception {
		List<String> allValues = getAllValues(objectXpath, objectName);
		String actualVisibleText = null;
		outerloop: for (String value : allValues) {
			if (StringUtils.containsIgnoreCase(value, optionsToCheck)) {
				actualVisibleText = value;
				break outerloop;
			}
		}
		if (actualVisibleText != null) {
			selectByVisibleText(objectXpath, objectName, actualVisibleText);
		} else {
			logger.error(String.format(optionsToCheck + " options not found for dropdown %s", objectName));
			throw new BishopException(String.format(optionsToCheck + " options not found for dropdown %s", objectName));
		}
	}

	public void selectByVisibleText(String objectXpath, String objectName, String visibleText) throws Exception {
		selectByVisibleText(objectXpath, objectName, visibleText, Browser.DEFAULT_DRIVER_TIMEOUT);
	}

	public void selectByVisibleTextIgnoreBlank(String objectXpath, String objectName, String visibleText)
			throws Exception {
		if (StringUtils.isNotBlank(visibleText)) {
			selectByVisibleText(objectXpath, objectName, visibleText, Browser.DEFAULT_DRIVER_TIMEOUT);
		}
	}

	public void selectByVisibleText(String objectXpath, String objectName, String visibleText, int timeout)
			throws Exception {
		Select select = null;
		try {
			select = new Select(G.elements.object(objectXpath));
			select.selectByVisibleText(visibleText);
			logger.info("Selected " + visibleText + " of the dropdown " + objectName);
		} catch (NoSuchElementException e) {
			throw new BishopException(String.format("%s is not an option for dropdown %s", visibleText, objectName));
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(
					String.format("Failed to select option %s from the %s dropdown", visibleText, objectName), e);
		}
	}

	public List<String> getAllSelectedOptionsTexts(String objectXpath, String objectName) {
		List<WebElement> options = getAllSelectedOptions(objectXpath, objectName);
		List<String> optionsTexts = new ArrayList<>();
		for (WebElement e : options) {
			optionsTexts.add(e.getText());
		}
		return optionsTexts;
	}

	public List<WebElement> getAllSelectedOptions(String objectXpath, String objectName) {
		return getAllSelectedOptions(objectXpath, objectName, Browser.DEFAULT_DRIVER_TIMEOUT);

	}

	public List<WebElement> getAllSelectedOptions(String objectXpath, String objectName, int timeout) {
		Select select = null;
		List<WebElement> listOfSelectedElements = null;
		try {
			select = new Select(G.elements.object(objectXpath, timeout));
			listOfSelectedElements = select.getAllSelectedOptions();
			logger.info("Selected options of the " + objectName + "dropdown are: " + listOfSelectedElements);
			return listOfSelectedElements;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public String getSelectedOptionText(String objectXpath, String objectName) {
		return getSelectedOption(objectXpath, objectName).getText();
	}

	public WebElement getSelectedOption(String objectXpath, String objectName) {
		Select select = null;
		WebElement selectedElement = null;
		try {
			select = new Select(G.elements.object(objectXpath));
			selectedElement = select.getFirstSelectedOption();
			logger.info(selectedElement.getText() + " is selected of the dropdown " + objectName);
			return selectedElement;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public List<String> getAllValues(String objectXpath, String objectName) {
		List<WebElement> listOfAllElements = getAllOptions(objectXpath, objectName);
		List<String> listElementValues = new ArrayList<>();
		for (WebElement element : listOfAllElements) {
			listElementValues.add(element.getText());
		}
		logger.info("Selected options of the " + objectName + "dropdown are: " + listElementValues);
		return listElementValues;
	}

	public List<WebElement> getAllOptions(String objectXpath, String objectName) {
		Select select = null;
		List<WebElement> listOfAllElements = null;
		try {
			select = new Select(G.elements.object(objectXpath));
			listOfAllElements = select.getOptions();
			return listOfAllElements;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * Select standard dropdown in windows application. The item selection is case
	 * sensitive
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param optionToSelect
	 * @throws Exception
	 * @throws BishopRuleViolationException
	 */
	public void selectWindowsDropown(String locType, String locatorName, String objectName, String optionToSelect)
			throws Exception, BishopRuleViolationException {
		selectWindowsDropown(locType, locatorName, objectName, optionToSelect, true, true);
	}

	public void selectWindowsDropown(String locType, String locatorName, String objectName, String optionToSelect,
			boolean caseSensitive) throws Exception, BishopRuleViolationException {
		selectWindowsDropown(locType, locatorName, objectName, optionToSelect, caseSensitive, true);
	}

	/**
	 * Select standard dropdown in windows application.
	 * 
	 * @param locType
	 * @param locatorName
	 * @param objectName
	 * @param optionToSelect
	 * @param caseSensitive  - This determines the case sensitivity of the option
	 *                       selection
	 * @throws Exception
	 * @throws BishopRuleViolationException
	 */
	public void selectWindowsDropown(String locType, String locatorName, String objectName, String optionToSelect,
			boolean caseSensitive, boolean optionsWithinDropdown) throws Exception, BishopRuleViolationException {
		try {
			if (StringUtils.isNotBlank(optionToSelect)) {
				WebElement dropdown = G.elements.winElement(locType, locatorName);
				dropdown.click();

				WebElement dropdownOption;

				if (caseSensitive) {
					if (optionsWithinDropdown) {
						dropdownOption = dropdown.findElement(By.name(optionToSelect));
					} else {
						dropdownOption = G.elements.winElement(Elements.NAME, optionToSelect);
					}

				} else {
					String xpathForOption = String.format(
							"//*[translate(@Name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz') = '%s']",
							optionToSelect.toLowerCase());
					if (optionsWithinDropdown) {
						dropdownOption = dropdown.findElement(By.xpath(xpathForOption));
					} else {
						dropdownOption = G.elements.winElement(Elements.XPATH, xpathForOption);
					}

				}
				dropdownOption.click();

				logger.info(optionToSelect + " is selected from the dropdown " + objectName);
			}
		} catch (Exception e) {
			logger.error("Error while selecting dropdown " + optionToSelect, e);
			throw new Exception("Unable to select option " + optionToSelect + " from the dropdown: " + objectName);
		}
	}

	/**
	 * Select type ahead dropdown in windows application.
	 * 
	 * @param xpath
	 * @param objectName
	 * @param optionToSelect
	 * @throws Exception
	 */
	public void selectTypeAheadDropdown(String xpath, String objectName, String optionToSelect) throws Exception {
		selectTypeAheadDropdown(xpath, objectName, optionToSelect, false);
	}

	/**
	 * Select type ahead dropdown in windows application.
	 * 
	 * @param xpath
	 * @param objectName
	 * @param optionToSelect
	 * @param partialMatch
	 * @throws Exception
	 */
	public void selectTypeAheadDropdown(String xpath, String objectName, String optionToSelect, boolean partialMatch)
			throws Exception {
		try {
			if (StringUtils.isNotBlank(optionToSelect)) {
				WebElement element = G.elements.object(xpath);
				element.clear();
				element.sendKeys(optionToSelect);
				G.wait.sleep(1);
				element.sendKeys(Keys.BACK_SPACE);
				G.wait.sleep(5);
				element.sendKeys(Keys.ARROW_DOWN);
				G.wait.sleep(1);
				element.sendKeys(Keys.TAB);

				G.wait.sleep(1);

				element = G.elements.object(xpath);
				String optionSelected = element.getAttribute("value");

				if (partialMatch) {
					if (!StringUtils.containsIgnoreCase(optionSelected, optionToSelect)) {
						throw new BishopException(
								"Option " + optionToSelect + " not present in dropdown: " + objectName);
					}
				} else {
					if (!optionSelected.equalsIgnoreCase(optionToSelect)) {
						throw new BishopException(
								"Option " + optionToSelect + " not present in dropdown: " + objectName);
					}
				}
			}
		} catch (BishopException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to select dropdown: " + objectName, e);
			throw new Exception("Unable to select dropdown: " + objectName, e);
		}
	}

	/**
	 * Select multiple options from multi select box
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @param optionsToSelect
	 * @throws Exception
	 */
	public void multiSelectByVisibleText(String objectXpath, String objectName, List<String> optionsToSelect)
			throws Exception {
		String optionNotFound = "";
		try {
			if (CollectionUtils.isNotEmpty(optionsToSelect)) {
				Select select = new Select(G.elements.object(objectXpath));
				for (String option : optionsToSelect) {
					optionNotFound = option;
					select.selectByVisibleText(option);
					logger.info("Selected " + option + " from the multi select " + objectName);
					G.wait.sleepforMilliseconds(500);
				}
			}
		} catch (NoSuchElementException e) {
			throw new BishopException(String.format("%s is not an option for dropdown %s", optionNotFound, objectName));
		} catch (Exception e) {
			throw new Exception(
					String.format("Failed to select options %s from the %s dropdown", optionsToSelect, objectName), e);
		}
	}
}
