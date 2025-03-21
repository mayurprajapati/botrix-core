package rpa.core.driver;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.slf4j.Logger;

import com.google.api.client.util.Maps;

import botrix.internal.logging.LoggerFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import rpa.core.entities.Constants;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.exceptions.BishopRuntimeException;
import rpa.core.file.FileHandlingUtils;
import rpa.core.file.OSValidator;

public class Browser {
	private static final Logger logger = LoggerFactory.getLogger(Browser.class);
	public static final int DEFAULT_DRIVER_TIMEOUT = 30;
	private static final String SAFARI_DRIVER_PROP = "webdriver.safari.driver";
	private static final String IE_DRIVER_PROP = "webdriver.ie.driver";
	private static final String GECKO_DRIVER_PROP = "webdriver.gecko.driver";
	private static final String CHROME_DRIVER_PROP = "webdriver.chrome.driver";
	private static final String BASE_DRIVER_PATH = "";
	private static final String IE_DRIVER_PATH = BASE_DRIVER_PATH + "/iedriver.exe";
	private static final String SAFARI_DRIVER_PATH = BASE_DRIVER_PATH + "safaridriver";
	private static final String FIREFOX_DRIVER_PATH_WIN = BASE_DRIVER_PATH + "/geckodriver.exe";
	private static final String CHROME_DRIVER_PATH_WIN = BASE_DRIVER_PATH + "/chromedriver.exe";
	private static final String FIREFOX_DRIVER_PATH_MAC = BASE_DRIVER_PATH + "/geckodriver";
	private static final String FIREFOX_DRIVER_PATH_LINUX = "/usr/bin/geckodriver";
	private static final String CHROME_DRIVER_PATH_MAC = BASE_DRIVER_PATH + "/chromedriver";
	private static boolean headless = false;
	private static boolean disableAutomationPlugin = false;
	private static boolean saveAsPDF = false;
	private static boolean alwaysTranslateToEnglish = false;
	private static boolean disableLocation = false;
	public static DriverService driverService;

	public String getBrowserProfile() {
		return System.getProperty("bishop.browserProfile");
	}

	public void initiate(String browser) {
		try {
			G.driver = initiateBrowser(browser);
			G.driver.manage().window().setSize(new Dimension(1920, 1080));
			G.driver.manage().timeouts().implicitlyWait(DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS);
			logger.info(String.format("Resolution details >> Height: %s || Width: %s",
					G.driver.manage().window().getSize().getHeight(), G.driver.manage().window().getSize().getWidth()));
		} catch (Exception e) {
			throw new BishopRuntimeException(String.format("Unable to start browser"), e);
		}
	}

	public RemoteWebDriver initiateBrowser(String browser) throws Exception {
		logger.debug("Starting browser " + browser);
		RemoteWebDriver driver = null;
		if (StringUtils.containsIgnoreCase(browser, "chrome"))
			WebDriverManager.chromedriver().setup();
		if (StringUtils.containsIgnoreCase(browser, "firefox"))
			WebDriverManager.firefoxdriver().setup();
		switch (browser.trim().toLowerCase()) {
		case "chrome":
			driver = chrome();
			break;
		case "firefox":
			driver = firefox();
			break;
		case "ie11":
			driver = ie11();
			break;
		case "safari":
			driver = safari();
			break;
		case "chrome-profile":
			driver = chromeProfile();
			break;
		case "firefox-profile":
			driver = firefoxProfile();
			break;
		case "ie11-profile":
			driver = ie11Profile();
			break;
		case "safari-profile":
			driver = safariProfile();
			break;
		case Constants.CHROME_PROXY:
			driver = chromeprofileproxy();
			break;
		default:

		}
		logger.info("Browser initiated " + browser);

		return driver;

	}

	public void setup() {
		G.driver.manage().window().setSize(new Dimension(1920, 1080));
		G.driver.manage().timeouts().implicitlyWait(DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS);
		logger.info(String.format("Resolution details >> Height: %s || Width: %s",
				G.driver.manage().window().getSize().getHeight(), G.driver.manage().window().getSize().getWidth()));
	}

	public RemoteWebDriver chromeprofileproxy() throws BishopRuleViolationException {
		logger.info("Starting chrome with proxy");
		String downloadFilepath = SystemProperties.DEFAULT_DOWNLOAD_LOCATION;
		FileHandlingUtils.mkdir(downloadFilepath);
		FileHandlingUtils.cleanDirectory(downloadFilepath);
		String proxyUrl = "127.0.0.1:24000";
		Proxy proxy = new Proxy();
		proxy.setProxyType(ProxyType.MANUAL);
		proxy.setHttpProxy(proxyUrl);
		proxy.setFtpProxy(proxyUrl);
		proxy.setSslProxy(proxyUrl);

		Map<Object, Object> chromePrefs = Maps.newHashMap();
		Map<String, Object> langs = new HashMap<String, Object>();
		ChromeOptions options = new ChromeOptions();
		langs.put("pl", "en");
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("download.prompt_for_download", false);
		chromePrefs.put("download.directory_upgrade", true);
		chromePrefs.put("plugins.always_open_pdf_externally", true);
		chromePrefs.put("safebrowsing.enabled", false);
		chromePrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);
		if (disableLocation)
			chromePrefs.put("profile.managed_default_content_settings.geolocation", 2);
		if (isAlwaysTranslateToEnglish()) {
			chromePrefs.put("translate", "{'enabled' : true}");
			chromePrefs.put("translate_whitelists", langs);
			options.addArguments("--lang=en");
		}
		if (isSaveAsPDF()) {
//		to print preview as PDF without asking. 
//		Doesn't work in headless. Bug: https://bugs.chromium.org/p/chromium/issues/detail?id=924981
			chromePrefs.put("printing.print_preview_sticky_settings.appState",
					"{\"recentDestinations\":{\"id\":\"Save as PDF\",\"origin\":\"local\",\"account\":\"\"},\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
		}

		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--disable-notifications");
		options.addArguments("--proxy-server=" + proxy);
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--no-sandbox");
		if (isDisableAutomationPlugin())
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		if (isHeadless())
			options.addArguments("--headless");
		if (OSValidator.isMac()) {
			options.addArguments("--kiosk");
		}
		if (OSValidator.isWindows()) {
			options.addArguments("--start-maximized");
		}
		if (OSValidator.isUnix()) {
			options.addArguments("--start-maximized");
		}
		switch (G.getPlatform()) {
		case "Mac OS":
			return chrome(CHROME_DRIVER_PATH_MAC, options);
		case "Windows":
			return chrome(CHROME_DRIVER_PATH_WIN, options);
		case "Unix":
			return chrome(CHROME_DRIVER_PATH_MAC, options);
		default:
			return null;
		}
	}

	private RemoteWebDriver chrome(String driverPath, ChromeOptions options) {
		stopDriverService();
		driverService = new ChromeDriverService.Builder().usingAnyFreePort().build();
		ChromeDriver d = new ChromeDriver((ChromeDriverService) driverService, options);
		return d;
	}

	private RemoteWebDriver firefox(String driverPath) {
		stopDriverService();
		driverService = new GeckoDriverService.Builder().usingAnyFreePort().build();
		return new FirefoxDriver((GeckoDriverService) driverService);
	}

	private RemoteWebDriver ie11(String driverPath) {
		stopDriverService();
		driverService = new InternetExplorerDriverService.Builder().usingAnyFreePort().build();
		return new InternetExplorerDriver((InternetExplorerDriverService) driverService);
	}

	private RemoteWebDriver safari(String driverPath) {
		stopDriverService();
		driverService = new SafariDriverService.Builder().usingAnyFreePort().build();
		return new SafariDriver((SafariDriverService) driverService);
	}

	private RemoteWebDriver chromeProfile(String driverPath) {
		return new ChromeDriver();
	}

	private RemoteWebDriver firefoxProfile(String driverPath, FirefoxOptions options) {
		stopDriverService();
		driverService = new GeckoDriverService.Builder().usingAnyFreePort().build();
		RemoteWebDriver driver = new FirefoxDriver((GeckoDriverService) driverService, options);
		try {
			driver.manage().window().maximize();
		} catch (Exception e) {
			logger.error("Failed to maximize firefox browser.");
		}
		return driver;
	}

	private RemoteWebDriver ie11Profile(String driverPath) throws BishopRuleViolationException {
		String downloadFilepath = Paths.get(System.getProperty("user.home"), "Downloads").toString();
		logger.info("Setting default download path for internet explorer to " + downloadFilepath);
		SystemProperties.DEFAULT_DOWNLOAD_LOCATION = downloadFilepath;
		FileHandlingUtils.mkdir(downloadFilepath);
		FileHandlingUtils.cleanDirectory(downloadFilepath);
		stopDriverService();
		driverService = new InternetExplorerDriverService.Builder().usingAnyFreePort().build();
		return new InternetExplorerDriver((InternetExplorerDriverService) driverService, iEOptions());
	}

	private InternetExplorerOptions iEOptions() {
		InternetExplorerOptions options = new InternetExplorerOptions();
		options.introduceFlakinessByIgnoringSecurityDomains();
		options.ignoreZoomSettings();
		options.destructivelyEnsureCleanSession();
		options.requireWindowFocus();
		// options.disableNativeEvents();
		return options;
	}

	private RemoteWebDriver safariProfile(String driverPath) {
		stopDriverService();
		driverService = new SafariDriverService.Builder().usingAnyFreePort().build();
		return new SafariDriver((SafariDriverService) driverService);
	}

	private RemoteWebDriver chrome() throws BishopRuleViolationException {
		ChromeOptions options = new ChromeOptions();
		switch (G.getPlatform()) {
		case "Mac OS":
			return chrome(CHROME_DRIVER_PATH_MAC, options);
		case "Windows":
			return chrome(CHROME_DRIVER_PATH_WIN, options);
		case "Unix":
			return chrome(CHROME_DRIVER_PATH_MAC, options);
		default:
			return null;
		}
	}

	private RemoteWebDriver firefox() throws BishopRuleViolationException {
		switch (G.getPlatform()) {
		case "Mac OS":
			return firefox(FIREFOX_DRIVER_PATH_MAC);
		case "Windows":
			return firefox(FIREFOX_DRIVER_PATH_WIN);
		case "Unix":
			return firefox(FIREFOX_DRIVER_PATH_MAC);
		default:
			return null;
		}
	}

	private RemoteWebDriver ie11() throws BishopRuleViolationException {
		switch (G.getPlatform()) {
		case "Mac OS":
			return null;
		case "Windows":
			return ie11(IE_DRIVER_PATH);
		default:
			return null;
		}

	}

	private RemoteWebDriver safari() throws BishopRuleViolationException {
		switch (G.getPlatform()) {
		case "Mac OS":
			return null;
		case "Windows":
			return safari(SAFARI_DRIVER_PATH);
		default:
			return null;
		}
	}

	/**
	 * Browser level profile configuration to best run automation All chrome
	 * switches - https://peter.sh/experiments/chromium-command-line-switches/
	 * 
	 * @return
	 * @throws BishopRuleViolationException
	 */
	private RemoteWebDriver chromeProfile() throws BishopRuleViolationException {
		String downloadFilepath = SystemProperties.DEFAULT_DOWNLOAD_LOCATION;
		FileHandlingUtils.mkdir(downloadFilepath);
		FileHandlingUtils.cleanDirectory(downloadFilepath);
		Map<Object, Object> chromePrefs = Maps.newHashMap();
		Map<String, Object> langs = new HashMap<String, Object>();
		ChromeOptions options = new ChromeOptions();
		langs.put("pl", "en");
		langs.put("de", "en");
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("download.prompt_for_download", false);
		chromePrefs.put("download.directory_upgrade", true);
		chromePrefs.put("plugins.always_open_pdf_externally", true);
		chromePrefs.put("Browser.setDownloadBehavior", "allow");
//		chromePrefs.put("disable-popup-blocking", true);
		chromePrefs.put("safebrowsing.enabled", true);
		chromePrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);
		if (disableLocation)
			chromePrefs.put("profile.managed_default_content_settings.geolocation", 2);
		if (isAlwaysTranslateToEnglish()) {
			chromePrefs.put("translate", "{'enabled' : true}");
			chromePrefs.put("translate_whitelists", langs);
			options.addArguments("--lang=en");
		}
		if (isSaveAsPDF()) {
			// to print preview as PDF without asking.
			// Doesn't work in headless. Bug:
			// https://bugs.chromium.org/p/chromium/issues/detail?id=924981
			chromePrefs.put("printing.print_preview_sticky_settings.appState",
					"{\"recentDestinations\":{\"id\":\"Save as PDF\",\"origin\":\"local\",\"account\":\"\"},\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
			chromePrefs.put("savefile.default_directory", downloadFilepath);
		}
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--disable-notifications");
		options.addArguments("--kiosk-printing");
		options.addArguments("--disable-infobars");
		options.addArguments("--safebrowsing-disable-download-protection");
		options.addArguments("safebrowsing-disable-extension-blacklist");
		options.addArguments("--test-type");
		options.addArguments("--no-sandbox");
		options.addArguments("--incognito");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-default-apps");
		options.addArguments("--disable-extensions");

		if (isDisableAutomationPlugin())
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		if (isHeadless())
			options.addArguments("--headless");
		if (OSValidator.isMac()) {
			options.addArguments("--kiosk");
		}
		if (OSValidator.isWindows() || OSValidator.isUnix()) {
			options.addArguments("--start-maximized");
		}
//		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); // Bydefault it will accepts all popups.
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true); // Bydefault it will accepts all popups.

		switch (G.getPlatform()) {
		case "Mac OS":
			RemoteWebDriver driver = chrome(CHROME_DRIVER_PATH_MAC, options);
			Map<String, Object> commandParams = new HashMap<>();
			commandParams.put("cmd", "Page.setDownloadBehavior");
			Map<String, String> params = new HashMap<>();
			params.put("behavior", "allow");
			params.put("downloadPath", downloadFilepath);
			commandParams.put("params", params);

			return driver;
		case "Windows":
			return chrome(CHROME_DRIVER_PATH_WIN, options);
		case "Unix":
			return chrome(CHROME_DRIVER_PATH_MAC, options);
		default:
			return null;
		}
	}

	private RemoteWebDriver firefoxProfile() throws BishopRuleViolationException {
		FirefoxOptions options = new FirefoxOptions();
		options.addPreference("pdfjs.disabled", true);
		options.addPreference("browser.download.manager.showWhenStarting", false);
		options.addPreference("browser.download.manager.showAlertOnComplete", false);
		options.addPreference("browser.download.manager.focusWhenStarting", false);
		options.addPreference("browser.download.manager.closeWhenDone", true);
		options.addPreference("browser.download.manager.useWindow", false);
		options.addPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
		options.addPreference("browser.download.folderList", 2);
		if (isHeadless())
			options.addArguments("--headless");
//		options.addPreference("dom.webnotifications.enabled", false);
//		options.addPreference("browser.download.dir", downloadir);
		options.addPreference("browser.helperApps.neverAsk.saveToDisk", ignoreMimeTypes());
		options.addPreference("browser.helperApps.alwaysAsk.force", false);
		options.addPreference("browser.download.folderList", 2);
		options.setAcceptInsecureCerts(true);
		options.addPreference("browser.download.panel.shown", false);
		options.addPreference("plugin.scan.Acrobat", "99.0");
		options.addPreference("plugin.scan.plid.all", false);

		String profile = getBrowserProfile();
		if (StringUtils.isNotEmpty(profile))
			options.addArguments("-profile", profile);
//		options.setLogLevel(Level.OFF);
		switch (G.getPlatform()) {
		case "Mac OS":
			return firefoxProfile(FIREFOX_DRIVER_PATH_MAC, options);
		case "Windows":
			return firefoxProfile(FIREFOX_DRIVER_PATH_WIN, options);
		case "Unix":
			return firefoxProfile(FIREFOX_DRIVER_PATH_LINUX, options);
		default:
			return null;
		}
	}

	private RemoteWebDriver ie11Profile() throws BishopRuleViolationException {
		switch (G.getPlatform()) {
		case "Mac OS":
			return null;
		case "Windows":
			return ie11Profile(IE_DRIVER_PATH);
		default:
			return null;
		}
	}

	private RemoteWebDriver safariProfile() throws BishopRuleViolationException {
		switch (G.getPlatform()) {
		case "Mac OS":
			return null;
		case "Windows":
			return safariProfile(SAFARI_DRIVER_PATH);
		default:
			return null;
		}
	}

	private static boolean isHeadless() {
		return headless;
	}

	public static void startHeadless() {
		Browser.headless = true;
	}

	public static void setHeadless(boolean headless) {
		startHeadless();
	}

	private static boolean isDisableAutomationPlugin() {
		return disableAutomationPlugin;
	}

	public static void setDisableAutomationPlugin(boolean disableAutomationPlugin) {
		Browser.disableAutomationPlugin = disableAutomationPlugin;
	}

	private static boolean isSaveAsPDF() {
		return saveAsPDF;
	}

	public static void enableSaveAsPDF() {
		Browser.saveAsPDF = true;
	}

	private static boolean isAlwaysTranslateToEnglish() {
		return alwaysTranslateToEnglish;
	}

	public static void alwaysTranslateToEnglish() {
		Browser.alwaysTranslateToEnglish = true;
	}

	/**
	 * All file format to silently download using firefox Refer for list of all MIME
	 * types - https://www.sitepoint.com/mime-types-complete-list/
	 * 
	 * @return
	 */
	private String ignoreMimeTypes() {
		String[] mimeTypes = { "audio/aac", "application/x-abiword", "application/x-freearc", "video/x-msvideo",
				"application/vnd.amazon.ebook", "application/octet-stream", "image/bmp", "application/x-bzip",
				"application/x-bzip2", "application/x-zip-compressed", "application/x-compressed", "multipart/x-zip",
				"application/x-csh", "text/css", "application/csv", "text/csv", "application/msword",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/vnd.ms-fontobject", "application/epub+zip", "application/gzip", "image/gif", "text/html",
				"image/vnd.microsoft.icon", "text/calendar", "application/java-archive", "image/jpeg",
				"text/javascript", "application/json", "application/ld+json", "audio/midi audio/x-midi",
				"text/javascript", "audio/mpeg", "video/mpeg", "application/vnd.apple.installer+xml",
				"application/vnd.oasis.opendocument.presentation", "application/vnd.oasis.opendocument.spreadsheet",
				"application/vnd.oasis.opendocument.text", "audio/ogg", "video/ogg", "application/ogg", "audio/opus",
				"font/otf", "image/png", "application/pdf", "application/php", "application/vnd.ms-powerpoint",
				"application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.rar",
				"application/rtf", "application/x-sh", "image/svg+xml", "application/x-shockwave-flash",
				"application/x-tar", "image/tiff", "video/mp2t", "font/ttf", "text/plain", "application/vnd.visio",
				"audio/wav", "audio/webm", "video/webm", "image/webp", "font/woff", "font/woff2",
				"application/xhtml+xml", "application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/xml", "text/xml",
				"application/vnd.mozilla.xul+xml", "application/zip", "video/3gpp", "audio/3gpp2", "video/3gpp2",
				"audio/3gpp2", "application/x-7z-compressed", "application/x-unknown", "text/comma-separated-values",
				"application/download", "binary/octet-stream", "application/binary", "text/x-vcard", "message/rfc822",
				"multipart/related", "application/vnd.ms-xpsdocument", "application/oxps", "video/mp4",
				"application/vnd.ms-excel.sheet.macroEnabled.12", "application/vnd.ms-outlook", "application/x-msexcel",
				"application/excel", "application/x-excel", "application/vnd", "ms-excel", "video/quicktime",
				"video/x-sgi-movie", "application/x-nwc"

		};
		return String.join(",", mimeTypes);
	}

	public static void disableLocation() {
		disableLocation = true;
	}

	public static void stopDriverService() {
		if (Browser.driverService != null) {
			logger.info("Stopping browser service");
			Browser.driverService.stop();
		}
	}
}