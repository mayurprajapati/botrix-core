package appium.wrapper.driver;

import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.logging.LoggingPreferences;

import appium.wrapper.utils.NetUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrowserDriverBuilderOptions extends DriverBuilderOptions {
	private String debugHost = "127.0.0.1";

	private int debugPort = NetUtils.findFreePort();

	private boolean isHeadless = false;

	private boolean isIncognito = false;

	private boolean startMaximized = true;

	private LoggingPreferences loggingPreferences = null;

	private boolean enableAutomaticDownloads = true;

	private String defaultDownloadLocation = "";

	private boolean disableGeolocation = false;

	private String defaultLanguage = "en";

	private Dimension windowSize = null;

	private PageLoadStrategy pageLoadStrategy = PageLoadStrategy.NORMAL;

	private String userDataDir = "";

	// https://deviceatlas.com/blog/list-of-user-agent-strings#desktop

	private String userAgent = "";

	private List<String> translateTheseLanguagesToDefault = List.of("pl");

	private String browserExecutablePath = "";
}
