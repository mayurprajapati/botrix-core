package rpa.core.web;

import java.util.List;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;

public class Link {

	private static Logger logger = LoggerFactory.getLogger(Link.class);

	public List<WebElement> getAll(String objectXpath, String objectName) {
		try {
			List<WebElement> allLinks = G.elements.objects(objectXpath);
			logger.info("All links: " + allLinks);
			return allLinks;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public void printAll(String objectXpath, String objectName) {
		List<WebElement> allLinks = getAll(objectXpath, objectName);
		for (WebElement link : allLinks) {
			logger.info(link.getText());
		}
	}

	public WebElement get(String objectXpath, String objectName) {
		try {
			WebElement link = G.elements.object(objectXpath);
			logger.info(objectName + " has link " + link);
			return link;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public void click(String objectXpath, String objectName) throws Exception {
		try {
			G.elements.object(objectXpath).click();
			logger.info(objectName + " link is clicked");
		} catch (Exception e) {
			logger.error("", e);
			throw new Exception(String.format("Failed to click on link %s", objectName));
		}
	}

	/**
	 * Click on all links which matched by the given xpath with a delay of 2 seconds
	 * in between each click
	 * 
	 * @param objectXpath
	 * @param objectName
	 * @throws Exception
	 */
	public void clickAll(String objectXpath, String objectName) throws Exception {
		try {
			List<WebElement> allLinks = G.elements.objects(objectXpath);
			for (int i = 0; i < allLinks.size(); i++) {
				WebElement element = allLinks.get(i);
				try {
					G.button.click(element, objectName);
					G.wait.sleep(2);
				} catch (ElementClickInterceptedException e) {
					G.window.scrollElementIntoView(element);
					G.wait.sleep(2);
					G.button.click(element, objectName);
					G.wait.sleep(2);
				}
				allLinks = G.elements.objects(objectXpath); // To avoid staleElemnetException
			}
		} catch (Exception e) {
			logger.error("Unable to click on all links for: " + objectName, e);
			throw new Exception("Unable to click on all links for: " + objectName);
		}
	}
}
