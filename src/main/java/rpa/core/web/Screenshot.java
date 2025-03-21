package rpa.core.web;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopFrameworkException;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.FileHandlingUtils;
import rpa.core.file.ParseUtils;

public class Screenshot {

	private static Logger logger = LoggerFactory.getLogger(Screenshot.class);
	private static final String BASE_SCREENSHOT_PATH = Paths.get(System.getProperty("user.dir"), "screenshots")
			.toString();
	private static String DEFAULT_SCREENSHOT_PATH = StringUtils.EMPTY;
	private static boolean captureScreenshots = true;

	public static boolean isCaptureScreenshots() {
		return captureScreenshots;
	}

	public static void setCaptureScreenshots(boolean captureScreenshots) {
		Screenshot.captureScreenshots = captureScreenshots;
	}

	public static String take(String outputPath) {
		String scrShtName = Calendar.getInstance().getTimeInMillis() + ".jpeg";
		return take(outputPath, scrShtName);
	}

	public static String take(String outputPath, String scrShtName) {
		String scrnPath = "";
		if (captureScreenshots) {
			try {
				if (DriverUtils.isWebDriverActive()) {
					if (!"png".equals(FilenameUtils.getExtension(scrShtName))) {
						scrShtName = scrShtName.replaceAll("[^\\d\\sa-zA-Z._\\-\\(\\)]", "") + ".png";
					}
					String date = ParseUtils.now();
					if (StringUtils.isBlank(outputPath)) {
						outputPath = Paths.get(getDefaultScreenshotPath(), date).toString();
					}
					scrnPath = Paths.get(outputPath, scrShtName).toString();
					File scrDir = new File(scrnPath);
					File scrFile = ((TakesScreenshot) G.driver).getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(scrFile, scrDir);
					logger.debug("Screenshot saved at: " + scrnPath);
				} else if (!G.nonUI) {
					takeDesktop();
				}
			} catch (WebDriverException e) {
				logger.warn("webdriver exception " + e.getClass());
			} catch (Exception e) {
				logger.error("Error while saving screenshot. " + e);
				try {
					takeDesktop();
				} catch (Exception e1) {
					logger.warn("Screenshot not supported");
					if (!StringUtils.contains(e1.getMessage(), "Command not implemented")) {
					}
				}
			}
		}
		return outputPath;
	}

	public static String take() {
		try {
			if (captureScreenshots) {
				if (DriverUtils.isWebDriverActive()) {
					if (!G.window.isAlertPresent()) {
						String screenshotName = ParseUtils.now();
						if (DriverUtils.isWebDriverActive())
							screenshotName = screenshotName + "_"
									+ G.driver.getTitle().replaceAll("[^\\d\\sa-zA-Z._\\-\\(\\)]", "") + ".png";
						return take(getDefaultScreenshotPath(), screenshotName);
					}
				}
			}
		} catch (WebDriverException e) {
//			logger.warn("webdriver exception " + e.getClass());
		} catch (Exception e) {
			logger.error("", e);
			if (e != null && (StringUtils.containsIgnoreCase(e.getMessage(), "already closed")
					|| StringUtils.containsIgnoreCase(e.getMessage(), "Unable to locate window"))) {
				logger.info("Window was already closed. Screenshot not captured.");
			} else
				logger.error("Failed to capture screenshot. " + e.getMessage());
		}
		return "";
	}

	public void takeFullscreen() {
		// Screenshot fpScreenshot = new
		// AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		// ImageIO.write(fpScreenshot.getImage(),"PNG",new
		// File("D:///FullPageScreenshot.png"));
	}

	public static String getDefaultScreenshotPath() {
		DEFAULT_SCREENSHOT_PATH = Paths
				.get(BASE_SCREENSHOT_PATH, G.executionMetrics.getFlowId(), G.executionMetrics.getMasterUuid())
				.toString();
		File file = new File(DEFAULT_SCREENSHOT_PATH);
		if (file != null && !file.isDirectory()) {
			FileHandlingUtils.mkdir(DEFAULT_SCREENSHOT_PATH);
			try {
				FileHandlingUtils.cleanDirectory(DEFAULT_SCREENSHOT_PATH);
			} catch (BishopRuleViolationException e) {
				logger.error("", e);
			}
		}
		return DEFAULT_SCREENSHOT_PATH;
	}

	public static String getBaseScreenshotPath() {
		return BASE_SCREENSHOT_PATH;
	}

	public static void takeDesktop() throws AWTException, IOException {
		BufferedImage image = new Robot()
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "png", new File(getDefaultScreenshotPath(), ParseUtils.now()));
	}

	public static File ofElement(String locType, String locName, String objectName) throws BishopFrameworkException {
		WebElement el = G.elements.winElement(locType, locName);
		if (el == null)
			throw new BishopFrameworkException("Element not available. Can not take screenshot");

		try {
			return ((TakesScreenshot) el).getScreenshotAs(OutputType.FILE);
		} catch (Exception e) {
			Point point = el.getLocation();
			int height = el.getSize().getHeight();
			int width = el.getSize().getWidth();
			return usingCoordinates(point.getX(), point.getY(), width, height);
		}
	}

	public static File usingCoordinates(int x, int y, int width, int height) throws BishopFrameworkException {
		try {
			// Get entire page screenshot
			File screenshot = ((TakesScreenshot) G.driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);

			int totalW = x + width;
			int totalH = y + height;

			if (totalW > fullImg.getWidth())
				width -= totalW - fullImg.getWidth();
			if (totalH > fullImg.getHeight())
				height -= totalH - fullImg.getHeight();

			// Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot = fullImg.getSubimage(x, y, width, height);
			ImageIO.write(eleScreenshot, "png", screenshot);

			return screenshot;
		} catch (Exception e) {
			throw new BishopFrameworkException("Failed to take screenshot using coordinates", e);
		}
	}

	public static void disableScreenshots() {
		captureScreenshots = false;
	}

	public static void enableScreenshots() {
		captureScreenshots = true;
	}
}
