package appium.wrapper.driver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChromeDriverBuilderOptions extends BrowserDriverBuilderOptions {
	/**
	 * a "welcome" alert might show up on *nix-like systems asking whether you want
	 * to set chrome as your default browser, and if you want to send even more data
	 * to google. now, in case you are nag-fetishist, or a diagnostics data feeder
	 * to google, you can set this to False. Note: if you don't handle the nag
	 * screen in time, the browser loses it's connection and throws an Exception.
	 */
	private boolean suppressWelcome = true;

	/**
	 * uses the --no-sandbox option, and additionally does suppress the "unsecure
	 * option" status bar this option has a default of True since many people seem
	 * to run this as root (....) , and chrome does not start when running as root
	 * without using --no-sandbox flag.
	 */
	private boolean noSandbox = true;
}
