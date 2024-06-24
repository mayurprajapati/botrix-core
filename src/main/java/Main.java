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
		app.resolveRecaptcha();
		app.quit();
	}

	// test gRPC
//	public static void main(String[] args) {
//		var play = new PythonBridgeClient();
//		var host = "127.0.0.1";
//		var port = 56874;
//		var builder = BrowserDriverBuilder.chrome().wdmDefault().userDataDir("chrome").debugHost(host).debugPort(port)
//				.browserExecutablePath("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
//		var app = builder.build();
//		app.get("https://www.google.com/recaptcha/api2/demo");
//		play.resolveRecaptcha(host, port);
//		System.out.println("Done");
//		app.get("https://maps.google.com/");
//	}
}
