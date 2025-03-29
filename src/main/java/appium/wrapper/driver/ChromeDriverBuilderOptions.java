package appium.wrapper.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ChromeDriverBuilderOptions extends BrowserDriverBuilderOptions {
	/**
	 * a "welcome" alert might show up on *nix-like systems asking whether you want
	 * to set chrome as your default browser, and if you want to send even more data
	 * to google. now, in case you are nag-fetishist, or a diagnostics data feeder
	 * to google, you can set this to False. Note: if you don't handle the nag
	 * screen in time, the browser loses it's connection and throws an Exception.
	 */
	@Builder.Default
	private boolean suppressWelcome = true;

	/**
	 * uses the --no-sandbox option, and additionally does suppress the "unsecure
	 * option" status bar this option has a default of True since many people seem
	 * to run this as root (....) , and chrome does not start when running as root
	 * without using --no-sandbox flag.
	 */
	@Builder.Default
	private boolean noSandbox = true;

	@Builder.Default
	private List<File> extensions = new ArrayList<>();
}
