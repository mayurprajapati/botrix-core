package rpa.core.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;

public class MouseActions {

	private static Logger logger = LoggerFactory.getLogger(MouseActions.class);

	public void Click(String objectXpath, String objectName) {
		try {
			Actions action = new Actions(G.driver);
			action.moveToElement(G.elements.object(objectXpath)).click().build().perform();
			logger.info("Clicked on element: " + objectName);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void doubleClick(String objectXpath, String objectName) {
		try {
			Actions action = new Actions(G.driver);
			action.doubleClick(G.elements.object(objectXpath)).build().perform();
			logger.info("Clicked on element: " + objectName);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void sendKeys(String objectXpath, String objectName, String Value) {
		try {
			Actions action = new Actions(G.driver);
			action.sendKeys(objectXpath, Value).build().perform();
			logger.info("Clicked on element: " + objectName);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void hoverOnElement(WebElement element, String objectName) {
		try {
			Actions action = new Actions(G.driver);
			action.moveToElement(element).build().perform();
			logger.info("Hover on element: " + objectName);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
