package appium.wrapper.driver;

import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.logging.LoggingPreferences;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BrowserDriverBuilderOptions extends DriverBuilderOptions {
	@Builder.Default
	private boolean isHeadless = false;

	@Builder.Default
	private boolean isIncognito = false;

	@Builder.Default
	private boolean startMaximized = true;

	@Builder.Default
	private LoggingPreferences loggingPreferences = null;

	@Builder.Default
	private boolean enableAutomaticDownloads = true;

	@Builder.Default
	private String defaultDownloadLocation = "";

	@Builder.Default
	private boolean disableGeolocation = false;

	@Builder.Default
	private String defaultLanguage = "en";

	@Builder.Default
	private Dimension windowSize = null;

	@Builder.Default
	private PageLoadStrategy pageLoadStrategy = PageLoadStrategy.NORMAL;

	@Builder.Default
	private String userDataDir = "";

	// https://deviceatlas.com/blog/list-of-user-agent-strings#desktop
	@Builder.Default
	private String userAgent = "";

	@Builder.Default
	private List<String> translateTheseLanguagesToDefault = List.of("pl");

	@Builder.Default
	private String browserExecutablePath = "";

	@Builder.Default
	private boolean keepUserDataDir = false;
}
