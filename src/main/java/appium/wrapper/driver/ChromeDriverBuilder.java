package appium.wrapper.driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import appium.wrapper.utils.NetUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Setter;

public class ChromeDriverBuilder {
	@Setter
	private String debugHost = "127.0.0.1";

	@Setter
	private int debugPort = NetUtils.findFreePort();

	@Setter
	private boolean isHeadless = false;

	@Setter
	private boolean isIncognito = false;

	@Setter
	private boolean startMaximized = true;

	@Setter
	private boolean enableAutomaticDownloads = true;

	@Setter
	private String defaultDownloadLocation = "";

	@Setter
	private boolean disableGeolocation = false;

	@Setter
	private String defaultLanguage = "en";

	@Setter
	private Dimension windowSize = null;

	@Setter
	private PageLoadStrategy pageLoadStrategy = PageLoadStrategy.NORMAL;

	// https://deviceatlas.com/blog/list-of-user-agent-strings#desktop
	@Setter
	private String userAgent = "";

	@Setter
	private List<String> translateTheseLanguagesToDefault = List.of("pl");

	@Setter
	private String browserExecutablePath = "";

	public ChromeDriverBuilder wdmChrome(Consumer<WebDriverManager> setup) {
		var wdm = WebDriverManager.chromedriver();
		setup.accept(wdm);
		return this;
	}

	public ChromeDriverBuilder wdmDefault() {
		wdmChrome((wdm) -> {
			wdm.setup();
		});
		return this;
	}

	public ChromeDriverWrapper build() {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> experimentalPrefs = new HashMap<>();

		options.addArguments("--remote-debugging-host=" + debugHost);
		options.addArguments("--remote-debugging-port=" + String.valueOf(debugPort));
		options.addArguments("--disable-infobars");
		options.addArguments("--safebrowsing-disable-download-protection");
		options.addArguments("--safebrowsing-disable-extension-blacklist");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

		if (isHeadless) {
			options.addArguments("headless").addArguments("disable-gpu");
		}

		if (isIncognito) {
			options.addArguments("--incognito");
		}

		if (enableAutomaticDownloads) {
			experimentalPrefs.put("profile.default_content_settings.popups", 0);
			experimentalPrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
			experimentalPrefs.put("download.prompt_for_download", false);
			experimentalPrefs.put("download.directory_upgrade", true);
			experimentalPrefs.put("plugins.always_open_pdf_externally", true);
			experimentalPrefs.put("Browser.setDownloadBehavior", "allow");
			experimentalPrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);

			experimentalPrefs.put("printing.print_preview_sticky_settings.appState",
					"{\"recentDestinations\":{\"id\":\"Save as PDF\",\"origin\":\"local\",\"account\":\"\"},\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
		}

		if (windowSize != null) {
			options.addArguments("--window-size=%s,%s".formatted(windowSize.width, windowSize.height));
		}

		if (startMaximized) {
			options.addArguments("--start-maximized");
		}

		if (StringUtils.isNotBlank(defaultDownloadLocation)) {
			experimentalPrefs.put("download.default_directory", defaultDownloadLocation);
			experimentalPrefs.put("savefile.default_directory", defaultDownloadLocation);
		}

		if (disableGeolocation) {
			experimentalPrefs.put("profile.managed_default_content_settings.geolocation", 2);
		}

		if (StringUtils.isNotBlank(defaultLanguage)) {
			options.addArguments("--lang=" + defaultLanguage);
		}

		if (!translateTheseLanguagesToDefault.isEmpty()) {
			Map<String, Object> languages = translateTheseLanguagesToDefault.stream()
					.collect(Collectors.toMap(el -> el, el -> defaultLanguage));
			experimentalPrefs.put("translate", "{'enabled' : true}");
			experimentalPrefs.put("translate_whitelists", languages);
		}

		if (StringUtils.isNotBlank(userAgent)) {
			options.addArguments("--user-agent=" + userAgent);
		}

		options.addArguments("--no-default-browser-check");
		options.addArguments("--no-first-run");
		options.addArguments("--no-sandbox");
		options.setExperimentalOption("prefs", experimentalPrefs);
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		options.setPageLoadStrategy(pageLoadStrategy);
		options.addArguments("--disable-blink-features=AutomationControlled");

		if (StringUtils.isBlank(browserExecutablePath)) {
			browserExecutablePath = WebDriverManager.chromedriver().getBrowserPath().get().toString();
		}

		Process process = createBrowserProcess(options, true);
		ChromeOptions newOptions = new ChromeOptions();
		newOptions.setExperimentalOption("debuggerAddress", debugHost + ":" + String.valueOf(debugPort));
		return new ChromeDriverWrapper(new ChromeDriver(newOptions), process, options);
	}

	@SuppressWarnings("unchecked")
	private List<String> getArgsFromChromeOptions(ChromeOptions options) {
		try {
			Field argsField = options.getClass().getSuperclass().getDeclaredField("args");
			argsField.setAccessible(true);
			return new ArrayList<>((List<String>) argsField.get(options));
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert Chrome Options to args", e);
		}
	}

	private Process createBrowserProcess(ChromeOptions chromeOptions, boolean needPrintChromeInfo)
			throws RuntimeException {
		List<String> args = getArgsFromChromeOptions(chromeOptions);
		if (args == null) {
			throw new RuntimeException("can't open browser, args not found");
		}
		Process p = null;
		try {
			args.add(0, browserExecutablePath);
			p = new ProcessBuilder(args).start();
		} catch (Exception e) {
			throw new RuntimeException("Unable to open chrome browser", e);
		}

		Process browser = p;

		Thread outputThread = new Thread(() -> {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(browser.getInputStream()));
				String buff = null;
				while ((buff = br.readLine()) != null) {
					System.out.println(buff);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Thread errorPutThread = new Thread(() -> {
			try {
				BufferedReader er = new BufferedReader(new InputStreamReader(browser.getErrorStream()));
				String errors = null;
				while ((errors = er.readLine()) != null) {
					System.out.println(errors);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		if (needPrintChromeInfo) {
			outputThread.start();
			errorPutThread.start();
		}

		return browser;
	}
}
