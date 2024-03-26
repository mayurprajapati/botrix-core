import org.openqa.selenium.Keys;

import appium.wrapper.driver.BrowserDriverBuilder;
import appium.wrapper.locator.AppiumLocator;

public class Main {
	public static void main(String[] args) {
		var app = BrowserDriverBuilder.chrome().wdmDefault().build();
		app.get("https://google.com");
		var loc = AppiumLocator.byXpath("Search Box", "//textarea[@title=\"Search\"]");
		app.findOne(loc).click();
		app.findOne(loc).sendKeys("Java" + Keys.ENTER);
	}
}
