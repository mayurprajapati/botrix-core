package botrix.web.herokuapp;

import appium.wrapper.driver.BrowserDriverBuilder;
import appium.wrapper.driver.ChromeDriverBuilderOptions;
import appium.wrapper.locator.AppiumLocator;

public class Add_Remove_Elements {
	public static void main(String[] args) {
		var options = ChromeDriverBuilderOptions.builder().build();
		try {
			var addElement = AppiumLocator.byXpath("Add Element", "//button[.='Add Element']");
			var delete = AppiumLocator.byXpath("Delete", "//button[.='Delete']");

			var app = BrowserDriverBuilder.chrome().wdmDefault().withOptions(options).build();
			app.get("https://the-internet.herokuapp.com/add_remove_elements/");

			int times = 10;
			app.clickTimes(addElement, times);

			app.clickAll(delete);

			app.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
