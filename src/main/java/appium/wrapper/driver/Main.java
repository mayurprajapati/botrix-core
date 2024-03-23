package appium.wrapper.driver;

public class Main {
	public static void main(String[] args) {
		var app = new BrowserDriverBuilder().chrome().wdmChromeDefault().build();
	}
}
