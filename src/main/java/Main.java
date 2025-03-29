import java.io.File;
import java.util.List;

import appium.wrapper.driver.BrowserDriverBuilder;
import appium.wrapper.driver.ChromeDriverBuilderOptions;
import appium.wrapper.locator.AppiumLocator;
import appium.wrapper.utils.WaitUtils;

public class Main {
	public static void main(String[] args) {
		var options = ChromeDriverBuilderOptions.builder().userDataDir("E:\\user-data-dirs\\chrome\\script-automation")
//				.extensions(List.of(new File("E:\\eclipse-workspace2\\Selenium2\\CAPTCHA-Solver.crx")))
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
				.build();
		try {
			var app = BrowserDriverBuilder.chrome().wdmDefault().withOptions(options).build();
			app.get("https://www.kucoin.com/ucenter/signup");
			var loc = AppiumLocator.byXpath("Phone Number Input", "//div[@id='login_account_input']//input");
			WaitUtils.sleepRandomSeconds(1, 3);
			app.findOne(loc).sendKeys("6302389200");
			WaitUtils.sleepRandomSeconds(1, 3);
//			app.resolveRecaptcha();
			var signupButton = AppiumLocator.byXpath("Signup Button",
					"//button[@data-inspector=\"signup_confirm_btn\"]");
			app.findOne(signupButton).click();
			app.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
