package appium.wrapper.driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;

public class ChromeDriverBuilder {
	private ChromeDriverBuilderOptions driverBuilderOptions = ChromeDriverBuilderOptions.builder().build();

	public ChromeDriverBuilder wdmChrome(Consumer<WebDriverManager> setup) {
		var wdm = WebDriverManager.chromedriver();
		setup.accept(wdm);
		return this;
	}

	public ChromeDriverBuilder withOptions(ChromeDriverBuilderOptions options) {
		this.driverBuilderOptions = options;
		return this;
	}

	public ChromeDriverBuilder wdmDefault() {
		wdmChrome((wdm) -> {
			wdm.setup();
		});
		return this;
	}

	@SneakyThrows
	public ChromeDriverWrapper build() {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> experimentalPrefs = new HashMap<>();

		options.addArguments("--remote-debugging-host=" + driverBuilderOptions.getDebugHost());
		options.addArguments("--remote-debugging-port=" + String.valueOf(driverBuilderOptions.getDebugPort()));
		options.addArguments("--disable-infobars");
		options.addArguments("--safebrowsing-disable-download-protection");
		options.addArguments("--safebrowsing-disable-extension-blacklist");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

		if (driverBuilderOptions.isHeadless()) {
			options.addArguments("headless").addArguments("disable-gpu");
		}

		if (driverBuilderOptions.isIncognito()) {
			options.addArguments("--incognito");
		}

		if (driverBuilderOptions.isEnableAutomaticDownloads()) {
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

		if (driverBuilderOptions.getWindowSize() != null) {
			options.addArguments("--window-size=%s,%s".formatted(driverBuilderOptions.getWindowSize().width,
					driverBuilderOptions.getWindowSize().height));
		}

		if (driverBuilderOptions.isStartMaximized()) {
			options.addArguments("--start-maximized");
		}

		if (StringUtils.isNotBlank(driverBuilderOptions.getDefaultDownloadLocation())) {
			experimentalPrefs.put("download.default_directory", driverBuilderOptions.getDefaultDownloadLocation());
			experimentalPrefs.put("savefile.default_directory", driverBuilderOptions.getDefaultDownloadLocation());
		}

		if (driverBuilderOptions.isDisableGeolocation()) {
			experimentalPrefs.put("profile.managed_default_content_settings.geolocation", 2);
		}

		if (driverBuilderOptions.getLoggingPreferences() != null) {
			options.setCapability(ChromeOptions.LOGGING_PREFS, driverBuilderOptions.getLoggingPreferences());
		}

		if (StringUtils.isNotBlank(driverBuilderOptions.getDefaultLanguage())) {
			options.addArguments("--lang=" + driverBuilderOptions.getDefaultLanguage());
		}

		if (StringUtils.isNotBlank(driverBuilderOptions.getUserDataDir())) {
			String udd = Paths.get(System.getProperty("user.dir"), driverBuilderOptions.getUserDataDir()).toString();
			options.addArguments("--user-data-dir=" + udd);
			driverBuilderOptions.setKeepUserDataDir(true);
		} else {
			var temp = Files.createTempDirectory("chrome").toString();
			options.addArguments("--user-data-dir=" + temp);
			driverBuilderOptions.setKeepUserDataDir(false);
			driverBuilderOptions.setUserDataDir(temp);
		}

		if (!driverBuilderOptions.getTranslateTheseLanguagesToDefault().isEmpty()) {
			Map<String, Object> languages = driverBuilderOptions.getTranslateTheseLanguagesToDefault().stream()
					.collect(Collectors.toMap(el -> el, el -> driverBuilderOptions.getDefaultLanguage()));
			experimentalPrefs.put("translate", "{'enabled' : true}");
			experimentalPrefs.put("translate_whitelists", languages);
		}

		if (StringUtils.isNotBlank(driverBuilderOptions.getUserAgent())) {
			options.addArguments("--user-agent=" + driverBuilderOptions.getUserAgent());
		}

		options.addArguments("--no-default-browser-check");
		options.addArguments("--no-first-run");

		if (driverBuilderOptions.isNoSandbox()) {
			options.addArguments("--no-sandbox", "--test-type");
		}

		if (driverBuilderOptions.isSuppressWelcome()) {
			options.addArguments("--no-default-browser-check", "--no-first-run");
		}

		options.setExperimentalOption("prefs", experimentalPrefs);
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		options.setPageLoadStrategy(driverBuilderOptions.getPageLoadStrategy());
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.setExperimentalOption("useAutomationExtension", false);

		if (StringUtils.isBlank(driverBuilderOptions.getBrowserExecutablePath())) {
			driverBuilderOptions
					.setBrowserExecutablePath(WebDriverManager.chromedriver().getBrowserPath().get().toString());
		}

		Process process = createBrowserProcess(options, true, driverBuilderOptions);

		ChromeOptions newOptions = new ChromeOptions();
		newOptions.setExperimentalOption("debuggerAddress",
				driverBuilderOptions.getDebugHost() + ":" + String.valueOf(driverBuilderOptions.getDebugPort()));
		return new ChromeDriverWrapper(new ChromeDriver(newOptions), process, driverBuilderOptions);
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

	private Process createBrowserProcess(ChromeOptions chromeOptions, boolean needPrintChromeInfo,
			ChromeDriverBuilderOptions driverBuilderOptions) throws RuntimeException {
		List<String> args = getArgsFromChromeOptions(chromeOptions);
		if (args == null) {
			throw new RuntimeException("can't open browser, args not found");
		}
		Process p = null;
		try {
			args.add(0, driverBuilderOptions.getBrowserExecutablePath());
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
