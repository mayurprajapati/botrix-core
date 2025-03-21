package rpa.core.sikuli;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.sikuli.script.App;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.web.Screenshot;

/**
 * SikuliX actions to be used while automating desktop based applications.
 * SikuliX works only with an active viewports which means that automation can
 * only run when server has an active monitor (real or virtual)
 * 
 * @author aishvaryakapoor
 *
 */
public class Interaction {

	private App app = null;
	private Screen screen = null;
	private static Screen screen2;
	private static String system;
	private static String SIKULI_IMAGES_PATH;
	private static Logger LOGGER = LoggerFactory.getLogger(Interaction.class);

	private static void setupInteraction() {
		try {
			if (Objects.isNull(screen2) || Objects.isNull(system) || Objects.isNull(SIKULI_IMAGES_PATH)) {
				screen2 = new Screen();
				system = G.executionMetrics.getCurrentSystem();
				SIKULI_IMAGES_PATH = Paths
						.get(System.getProperty("user.dir"), "src", "main", "resources", "sikuli_images", system, "")
						.toString() + File.separator;
			} else {
				LOGGER.info("");
			}
		} catch (Exception e) {
			LOGGER.error("");
		}
	}

	public void clickImage(String imageName, String name, int secs, boolean screenshot, int right, int left, int below,
			int above) throws Exception {
		try {
			setupInteraction();
			Pattern p1 = new Pattern(SIKULI_IMAGES_PATH + imageName);
			G.wait.sleep(2);
			screen2.find(p1).highlight(secs).getCenter().right(right).left(left).below(below).above(above).hover()
					.click();
			G.wait.sleep(2);
			if (screenshot) {
				Screenshot.take();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void clickImage(String imageName, String name) throws Exception {
		clickImage(imageName, name, 2, true, 0, 0, 0, 0);
	}

	public void clickImageRightAt(String imageName, String name, int rightOffSet) throws Exception {
		clickImage(imageName, name, 2, true, rightOffSet, 0, 0, 0);
	}

	public void clickImageBelowAt(String imageName, String name, int belowOffset) throws Exception {
		clickImage(imageName, name, 2, true, 0, 0, belowOffset, 0);
	}

	public void clickImageLeftAt(String imageName, String name, int leftOffSet) throws Exception {
		clickImage(imageName, name, 2, true, 0, leftOffSet, 0, 0);
	}

	public void clickImageAboveAt(String imageName, String name, int aboveOffSet) throws Exception {
		clickImage(imageName, name, 2, true, 0, 0, 0, aboveOffSet);
	}

	public boolean isImagePresent(String imageName, String name, boolean screenshot) {
		boolean flag = false;
		try {
			setupInteraction();
			Pattern p1 = new Pattern(SIKULI_IMAGES_PATH + imageName);
			G.wait.sleep(2);
			flag = screen2.has(p1);
			G.wait.sleep(2);
			if (screenshot) {
				Screenshot.take();
			}
			G.wait.sleep(2);
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	public String getTextRightAt(String imageName, String name, int right) throws Exception {
		return getText(imageName, name, 2, true, right, 0, 0, 0, 0, 0);
	}

	public String getTextLeftAt(String imageName, String name, int left) throws Exception {
		return getText(imageName, name, 2, true, 0, left, 0, 0, 0, 0);
	}

	public String getTextbelowAt(String imageName, String name, int below) throws Exception {
		return getText(imageName, name, 2, true, 0, 0, below, 0, 0, 0);
	}

	public String getTextaboveAt(String imageName, String name, int above) throws Exception {
		return getText(imageName, name, 2, true, 0, 0, 0, above, 0, 0);
	}

	public String getTextGrow(String imageName, String name, int widthOffset, int heightOffset) throws Exception {
		return getText(imageName, name, 2, true, 0, 0, 0, 0, widthOffset, heightOffset);
	}

	public String getText(String imageName, String name, int secs, boolean screenshot, int right, int left, int below,
			int above, int widthOffset, int hieghtOffset) throws Exception {
		String text = StringUtils.EMPTY;
		try {
			setupInteraction();
			Pattern p1 = new Pattern(SIKULI_IMAGES_PATH + imageName);
			G.wait.sleep(2);
			text = screen2.find(p1).right(right).left(above).below(below).above(right).grow(widthOffset, hieghtOffset)
					.highlight(secs, "green").text();
			G.wait.sleep(2);
			if (screenshot) {
				Screenshot.take();
			}
		} catch (Exception e) {
			throw e;
		}
		return text;
	}

	public void doubleClickImage(String imageName, String name) throws Exception {
		doubleClickImage(imageName, name, 2, true, 0, 0, 0, 0);
	}

	public void doubleClickImageRightAt(String imageName, String name, int rightOffSet) throws Exception {
		doubleClickImage(imageName, name, 2, true, rightOffSet, 0, 0, 0);
	}

	public void doubleClickImageBelowAt(String imageName, String name, int belowOffset) throws Exception {
		doubleClickImage(imageName, name, 2, true, 0, 0, belowOffset, 0);
	}

	public void doubleClickImageLeftAt(String imageName, String name, int leftOffSet) throws Exception {
		doubleClickImage(imageName, name, 2, true, 0, leftOffSet, 0, 0);
	}

	public void doubleClickImageAboveAt(String imageName, String name, int aboveOffSet) throws Exception {
		doubleClickImage(imageName, name, 2, true, 0, 0, 0, aboveOffSet);
	}

	public void doubleClickImage(String imageName, String name, int secs, boolean screenshot, int right, int left,
			int below, int above) throws Exception {
		try {
			setupInteraction();
			Pattern p1 = new Pattern(SIKULI_IMAGES_PATH + imageName);
			G.wait.sleep(2);
			screen2.find(p1).highlight(secs).getCenter().right(right).left(left).below(below).above(above).hover()
					.doubleClick();
			G.wait.sleep(2);
			if (screenshot) {
				Screenshot.take();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Move the wheel at the current mouse position<br>
	 * the given steps in the given direction: Button.WHEEL_DOWN, Button.WHEEL_UP
	 *
	 * @param direction to move the wheel
	 * @param steps     the number of steps
	 * 
	 */
	public void scroll(int direction, int steps) {
		screen2.wheel(direction, steps);
	}

	public void scrollUp(int steps) {
		scroll(Button.WHEEL_UP, steps);
	}

	public void scrollDown(int steps) {
		scroll(Button.WHEEL_DOWN, steps);
	}

	public void scrollHorizontal(String sliderTitle, String sliderImage, int steps) throws Exception {
		try {
			setupInteraction();
			Pattern p1 = new Pattern(SIKULI_IMAGES_PATH + sliderTitle);
			Pattern p2 = new Pattern(SIKULI_IMAGES_PATH + sliderImage);
			G.wait.sleep(2);
			Region find = screen2.find(p1).right().find(p2).highlight(2);
			screen2.dragDrop(find, new Location(find.x - steps, find.y));
			Screenshot.take();
		} catch (Exception e) {
			Screenshot.take();
			throw e;
		}
	}

	public boolean isElementPresent(String element, String name) {
		return isElementPresent(new Pattern(element), name);
	}

	public boolean isElementPresent(Pattern element, String name) {
		try {
			checkVisibilityOfElement(element, name);
			return true;
		} catch (Exception e) {
			LOGGER.info(String.format("Element %s is not found", name));
		}
		return false;
	}

	public void checkVisibilityOfElement(Pattern element, String name) throws Exception {
		try {
			focus();
			G.wait.sleep(2);
			screen.wait(element, 90);
		} catch (FindFailed e) {
			LOGGER.error(
					String.format("Expected element not %s found. Possible reason - view to element blocked.", name),
					e);
			throw new Exception(
					String.format("Expected element not %s found. Possible reason - view to element blocked.", name),
					e);
		}
	}

//
	public void click(String button, String name) throws Exception {
		click(button, name, 0, 0);
	}

	public void click(String button, String name, int xOffset, int yOffset) throws Exception {
		clickOn(new Pattern(button).targetOffset(xOffset, yOffset), name);
	}

	public void clickOn(Pattern button, String name) throws Exception {
		try {
			focus();
			screen.wait(button, 90);
			screen.click(button);
		} catch (FindFailed e) {
			LOGGER.error("Failed to find button.", e);
			throw new Exception("Failed to find button.", e);
		}
	}

	public void typeValue(String value, String name) throws Exception {
		try {
			focus();
			screen.type(Key.END);
			G.wait.sleep(1);
			screen.type(KeyModifier.SHIFT, Key.HOME);
			G.wait.sleep(1);
			for (int i = 0; i < 10; i++) {
				screen.type(Key.BACKSPACE);
				G.wait.sleep(1);
			}
			focus();
			screen.type(value);
			Thread.sleep(1000);
		} catch (FindFailed e) {
			LOGGER.error("Unable to find element.", e);
			throw new Exception("Unable to find element.", e);
		} catch (Exception e) {
			LOGGER.error(String.format("Failed to type %s.", value), e);
			throw new Exception(String.format("Failed to type %s.", value), e);
		}
	}

	public void registerApp(String appPath, String windowTitle, String name) {
		screen = new Screen();
		app = new App(appPath);
		app = App.focus(windowTitle);
	}

	public void focus() {
		app.focus();
	}

	public void focus(String windowTitle) {
		App.focus(windowTitle);
	}

	public App getApp() {
		return app;
	}

	public Screen getScreen() {
		if (!Objects.isNull(screen)) {
			return screen;
		} 
		return screen2;

	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

}
