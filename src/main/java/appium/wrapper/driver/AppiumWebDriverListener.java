package appium.wrapper.driver;

import java.lang.reflect.Method;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;

public class AppiumWebDriverListener implements WebDriverListener {
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AppiumWebDriverListener.class);

	@Override
	public void afterAccept(Alert alert) {
		LOG.info("Alert '{}' was accepted", alert.getText());
	}

	@Override
	public void afterAnyNavigationCall(Navigation navigation, Method method, Object[] args, Object result) {

	}
}
