package appium.wrapper.driver;

import java.lang.reflect.Method;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.support.events.WebDriverListener;

import botrix.utils.HasLogger;

public class AppiumWebDriverListener implements WebDriverListener, HasLogger {

	@Override
	public void afterAccept(Alert alert) {
		LOGGER.info("Alert '{}' was accepted", alert.getText());
	}

	@Override
	public void afterAnyNavigationCall(Navigation navigation, Method method, Object[] args, Object result) {

	}
}
