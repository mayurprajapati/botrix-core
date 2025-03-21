package rpa.core.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.security.Security;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import botrix.internal.logging.LoggerFactory;
import io.appium.java_client.windows.WindowsDriver;
import rpa.core.driver.Browser;
import rpa.core.driver.G;
import rpa.core.driver.SystemProperties;
import rpa.core.entities._BotInfo;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.exceptions.BishopTimeoutException;
import rpa.core.file.FileHandlingUtils;
import rpa.core.file.OSValidator;
import rpa.core.metrics.Helper;
import rpa.core.vpn.CiscoAnyconnectVPN;
import rpa.core.web.Elements;
import rpa.core.web.Screenshot;
import rpa.core.windowsprocess.WindowsProcess;

public class SetupHooks {

	private static final Logger logger = LoggerFactory.getLogger(SetupHooks.class);

	/**
	 * For old bots
	 * 
	 * @param flowId
	 * @param BishopAccount
	 * @param bot
	 * @param object
	 * @param systems
	 * @throws Exception
	 */
	public void setup(String flowId, String BishopAccount, String bot, String object, String systems) throws Exception {
		setup(flowId, BishopAccount, bot, object, systems, null);
	}

	public void setup(String flowId, String BishopAccount, String bot, String object, String systems, _BotInfo _botInfo)
			throws Exception {
//		if (G.testProps != null && BooleanUtils.toBoolean((String) G.testProps.get("isBotAlreadySetup"))) {
//			logger.info("Bot is already setup, skipping...");
//			return;
//		}
		Thread.currentThread().setName(Helper.createUUID());
		Security.setProperty("crypto.policy", "unlimited");
		System.setProperty("GOOGLE_CLOUD_PROJECT", "Bishop-ipa");
		// Property to allow auto download missing certs to avoid PKIX exception
		System.setProperty("com.sun.security.enableAIAcaIssuers", "true");
		logger.info("Starting execution....");
	}

	public void launchBrowser(String browserProfile) throws Exception {
//		updateBotInfoInFirebase();
		G.nonUI = false;
		G.webApp = true;
		connectRemoteMachine("webApp");
		startBrowser(browserProfile);
		G.jse = (JavascriptExecutor) G.driver;
	}

	public boolean launchWindowsDriverWithRetry(String path, int retry) throws Exception {
		return launchWindowsDriverWithRetry(path, "", "", "", retry);
	}

	public boolean launchWindowsDriverWithRetry(String path, String remoteUrl, String filePath, String requiredWindow,
			int retry) throws Exception {
		return launchWindowsDriverWithRetry(path, remoteUrl, filePath, requiredWindow, retry, true);
	}

	public boolean launchWindowsDriverWithRetry(String path, String remoteUrl, String filePath, String requiredWindow,
			int retry, boolean maximizeWindow) throws Exception {

		for (int i = 0; i < retry; i++) {
			try {
				launchWindowsDriver(path, remoteUrl, filePath, requiredWindow, maximizeWindow);
				return true;
			} catch (Exception e) {
				if (i == retry - 1) {
					throw new Exception("Error in launching application. No. of retry: " + retry, e);
				} else {
					logger.error("Error in launching application with path : " + path, e);
					G.wait.sleep(20);
				}
			}
		}
		return false;

	}

	public void launchWindowsDriver(String path, boolean maximizeWindow) throws Exception {
		launchWindowsDriver(path, "", "", "", maximizeWindow);
	}

	public void launchWindowsDriver(String path) throws Exception {
		launchWindowsDriver(path, "", "", "", true);
	}

	public void launchWindowsDriver(String path, String remoteUrl, String filePath, String requiredWindow)
			throws Exception {
		launchWindowsDriver(path, remoteUrl, filePath, requiredWindow, true);
	}

	/**
	 * @param path           - application executable path.
	 * @param remoteUrl
	 * @param filePath       - file to open with the application. Any flag to be
	 *                       included (Ex- /e to skip splash screen for Excel)
	 * @param requiredWindow - title of required window to switch after app launch
	 * @throws Exception
	 */
	public void launchWindowsDriver(String path, String remoteUrl, String filePath, String requiredWindow,
			boolean maximizeWindow) throws Exception {
		G.windowsApp = true;
		connectRemoteMachine("windowsApp");

		try {
			G.nonUI = false;

			if (StringUtils.isBlank(remoteUrl))
				runWinAppDriver();

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("app", path);
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			if (StringUtils.isNotBlank(filePath)) {
				capabilities.setCapability("appArguments", filePath);
			}
			capabilities.setPlatform(Platform.WINDOWS);
			if (StringUtils.isBlank(remoteUrl)) {
				G.driver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
			} else {
				logger.info("Starting WAD remotely on " + remoteUrl);
				G.driver = (WindowsDriver) (new WindowsDriver(new URL(remoteUrl), capabilities));
			}
			G.driver.manage().timeouts().implicitlyWait(Browser.DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS);
			G.wait.sleep(10);
			if (StringUtils.isNotBlank(requiredWindow)) {
				logger.info(String.format("Switching to window '%s'", requiredWindow));
				G.window.switchToWindowWithTitleContains(requiredWindow, 30, false);
			}
		} catch (Exception e) {
			String msg = "Error in launching windows driver in without windowHandle";
			logger.error(msg, e);
			if (StringUtils.isNotBlank(requiredWindow)) {
				logger.info(String.format("Trying to get window '%s' from root", requiredWindow));
				launchWindowsDriverWithRoot(requiredWindow);
			} else {
				throw new Exception(msg, e);
			}
		}

		if (maximizeWindow) {
			try {
				G.driver.manage().window().maximize();
			} catch (Exception e) {
				logger.error("Not able to maximize application : " + path);
			}
		}

	}

	public void connectRemoteMachine(String applicationType) throws Exception {
//		String BishopAccount = System.getProperty("BishopAccount");
//		String hostName = getHostName();
//		String remoteDesktopId = String.format("%s_%s", BishopAccount, hostName);
//
//		if (isViewportRequired(applicationType)) {
//			try {
//				if (isMachineLocked()) {
//					DocumentSnapshot remoteDesktop = FirestoreDB.db.collection("automation-desktop")
//							.document(remoteDesktopId).get().get();
//
//					boolean isRdpConnection = false;
//					if (remoteDesktop.contains("rdpConnection")) {
//						isRdpConnection = remoteDesktop.getBoolean("rdpConnection");
//					}
//
//					// lock only if it's not RDP machine
//					if (!isRdpConnection)
//						lockThisWorkStation();
//					requestToConnectUsingVnc(remoteDesktopId);
//				}
//			} catch (Exception e) {
//				throw new Exception(
//						String.format("Connection to remote machine '%s' is not established.", remoteDesktopId), e);
//			}
//		}
	}

	public void disconnectRemoteMachine(String applicationType) {
		try {
			String BishopAccount = System.getProperty("BishopAccount");
			String hostName = getHostName();
			String remoteDesktopId = String.format("%s_%s", BishopAccount, hostName);

			if (isViewportRequired(applicationType)) {
				requestToDisconnectRemoteSession(remoteDesktopId);
			}
		} catch (Exception e) {
			logger.error("Unable to send request to disconnect RDP session for remote machine", e);
		}
	}

	public boolean isViewportRequired(String applicationType) {
		boolean isViewportRequired = false;

		try {
			if (G.windowsApp && StringUtils.equals(applicationType, "windowsApp")) {
				boolean viewportRequired = G.executionMetrics.getFlow().getFeatureToggle()
						.getCustomBoolean("viewportRequired");
				if (viewportRequired) {
					isViewportRequired = true;
				}
			} else if (G.webApp && StringUtils.equals(applicationType, "webApp")) {
				boolean viewportRequiredForWebAutomation = G.executionMetrics.getFlow().getFeatureToggle()
						.getCustomBoolean("viewportRequiredForWebAutomation");
				if (viewportRequiredForWebAutomation) {
					isViewportRequired = true;
				}
			}
		} catch (Exception e) {
			isViewportRequired = false;
		}
		return isViewportRequired;
	}

	public void launchWindowsDriverWithRoot(String requiredWindow) throws Exception {
		launchWindowsDriverWithRoot(requiredWindow, true);
	}

	public static void launchWindowsDriverWithRoot(String requiredWindow, boolean maximizeWindow) throws Exception {
		launchWindowsDriverWithRoot(requiredWindow, true, 30);
	}

	public static void launchWindowsDriverWithRoot(String requiredWindow, boolean maximizeWindow, int waitTimeInSecond)
			throws Exception {
		launchWindowsDriverWithRoot(requiredWindow, maximizeWindow, waitTimeInSecond, Elements.NAME);
	}

	public static void launchWindowsDriverWithRoot(String requiredWindow, boolean maximizeWindow, int waitTimeInSecond,
			String locatorType) throws Exception {
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("app", "Root");
			WindowsDriver driver = new WindowsDriver(new URL("http://127.0.0.1:4723"),
					capabilities);
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSecond));

			logger.info(String.format("Searching window '%s'..", requiredWindow));
			WebElement app = wait.until(
					ExpectedConditions.presenceOfElementLocated(G.elements.byLocator(locatorType, requiredWindow)));

			String winHandle = app.getAttribute("NativeWindowHandle");
			capabilities = new DesiredCapabilities();
			capabilities.setCapability("appTopLevelWindow", Integer.toHexString(Integer.valueOf(winHandle)));
			driver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
			G.driver = driver;
//			if (Boolean.FALSE.equals(ScreenRecorderUtil.isRecording())) {
//				ScreenRecorderUtil.startRecord();
//			}
			if (maximizeWindow) {
				try {
					G.driver.manage().window().maximize();
				} catch (Exception e) {
					logger.error("Not able to maximize window : " + requiredWindow);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Failed to locate opened application window with name %s", requiredWindow));
			throw new Exception("Failed to locate opened application window with name " + requiredWindow, e);
		} finally {
			try {
				G.window.implicitWaitDefault();
			} catch (Exception e) {
				logger.warn("Unable to change the implicitWait");
			}
		}
	}

	public static boolean launchWindowsDriverWithRoot(String requiredWindow, int retry) throws Exception {
		for (int i = 1; i <= retry; i++) {
			try {
				launchWindowsDriverWithRoot(requiredWindow, true, 1);
				return true;
			} catch (Exception e) {
				G.wait.sleep(5);
			}
		}
		return false;
	}

	public void runWinAppDriver() throws IOException {
		G.windowsApp = true;
		try {
			String path = "C:\\Program Files (x86)\\Windows Application Driver";
			ProcessBuilder pBuilder = new ProcessBuilder("cmd", "/C", "Start WinAppDriver.exe");
			pBuilder.directory(new File(path));
			pBuilder.redirectErrorStream();
			pBuilder.inheritIO();
			G.winAppProcess = pBuilder.start();
			logger.info("Win App Driver started");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(G.winAppProcess.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(G.winAppProcess.getErrorStream()));
			StringBuilder insBuilder = new StringBuilder();
			StringBuilder errsBuilder = new StringBuilder();
			String s = "";
			while ((s = stdInput.readLine()) != null) {
				insBuilder.append(s).append(System.lineSeparator());
			}
			while ((s = stdError.readLine()) != null) {
				errsBuilder.append(s).append(System.lineSeparator());
			}
			if (StringUtils.isNotBlank(errsBuilder)) {
				logger.error("Error for the command : " + errsBuilder);
			}
			String streamOutput = insBuilder.toString() + System.lineSeparator() + errsBuilder.toString();
			logger.info("Stream output >> " + streamOutput);
		} catch (Exception e) {
			logger.error("Error in starting win app driver", e);
		}
	}

	private void startBrowser(String browserProfile) throws Exception {
		if (StringUtils.isNotBlank(browserProfile)) {
			Browser browser = new Browser();
			browser.initiate(browserProfile);
			browser.setup();
		}
	}

	public void cleanupTemp() {
		try {
			String tempDir = File.createTempFile("temp-file", "tmp").getParent();
			logger.info("Cleaning temp: " + tempDir);
			List<File> allFilesToDelete = new ArrayList<>();
			allFilesToDelete.addAll(FileHandlingUtils.getListOfAllFiles(tempDir, "odbc*.ps1"));
			allFilesToDelete.addAll(FileHandlingUtils.getListOfAllFiles(tempDir, "io_grpc*.*"));
			for (File file : allFilesToDelete) {
				try {
					file.delete();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.error("Unable to delete temp files.", e);
		}
	}

	public void tearDown() {
//		ScreenRecorderUtil.stopRecord();
		if (G.executionMetrics.isVpn())
			CiscoAnyconnectVPN.disableAnyConnect();
		quitBrowser();
		disconnectRemoteMachine("webApp");
		cleanupTemp();
	}

	/**
	 * Tear down method used in windows application automation
	 */
	public void tearDownWinAppDriver() {
		if (G.executionMetrics.isVpn())
			CiscoAnyconnectVPN.disableAnyConnect();
		closeWindowsApplication();
		closeWinAppDriver();
		disconnectRemoteMachine("windowsApp");
//		ScreenRecorderUtil.stopRecord();
		cleanupTemp();
	}

	public void lockRemoteMachine(String applicationType) {
		if (isViewportRequired(applicationType)) {
			lockThisWorkStation();
		}
	}

	public void closeBrowser() throws Exception {
		try {
			Screenshot.take();
			G.driver.close();
		} catch (Exception e) {
			logger.error("Failed to close browser. " + e);
			throw new Exception("Failed to close browser. " + e.getMessage());
		}
	}

	public void quitBrowser() {
		try {
			logger.debug("Closing browser");
			if (G.driver != null) {
				Screenshot.take();
				// Global.driver.close();
				G.driver.quit();
				logger.info("Browser closed");
			}
			Browser.stopDriverService();
		} catch (Exception e) {
			logger.error("Failed to quit browser");
		}
	}

	/**
	 * This method close the windows application opened by automation
	 */
	public void closeWindowsApplication() {
		try {
			logger.debug("Closing Windows application");
			if (G.driver != null) {
				((WindowsDriver) G.driver).quit();
				logger.info("All application windows closed");
			}
		} catch (Exception e) {
			logger.error("Failed to close application");
		}
	}

	/**
	 * This method close the WinAppDriver by using windows taskkill method
	 */
	public void closeWinAppDriver() {
		try {
			logger.debug("Closing WinAppDriver Server");
			if (G.winAppProcess != null) {
				WindowsProcess.killWindowsProcess("WinAppDriver.exe");
				logger.info("WinApp driver server closed");
			}
		} catch (Exception e) {
			logger.error("Failed to close WinAppDriver Server");
		}
	}

	private void lockThisWorkStation() {
		WindowsProcess.runCommandAndWait("Rundll32.exe user32.dll, LockWorkStation");
		logger.info("Remote machine locked successfully");
	}

	private void requestToConnectUsingVnc(String remoteDesktopId) throws Exception {
//		DocumentReference remoteDesktop = FirestoreDB.db.collection("automation-desktop").document(remoteDesktopId);
//		remoteDesktop.update("connectionFailed", false).get();
//		remoteDesktop.update("connectionSuccessful", false).get();
//		remoteDesktop.update("connectionRequired", true).get();
//		logger.info(String.format(
//				"Request sent to connect remote machine '%s' for active viewport. Waiting for response...",
//				remoteDesktopId));
//
//		int maxWaitTime = 120;
//		boolean connectionSuccessful = false;
//		boolean connectionFailed = false;
//		boolean lockStatusCheck = true;
//		G.wait.sleep(5);
//		for (int waitTime = 1; waitTime <= maxWaitTime; waitTime++) {
//			try {
//				DocumentSnapshot remoteDesktopStatus = FirestoreDB.db.collection("automation-desktop")
//						.document(remoteDesktopId).get().get();
//				connectionSuccessful = remoteDesktopStatus.getBoolean("connectionSuccessful");
//				connectionFailed = remoteDesktopStatus.getBoolean("connectionFailed");
//				if (connectionFailed || connectionSuccessful) {
//					if (remoteDesktopStatus.contains("rdpConnection")
//							&& remoteDesktopStatus.getBoolean("rdpConnection")) {
//						lockStatusCheck = false;
//					}
//
//					break;
//				}
//			} catch (Exception e) {
//				logger.error("Unable to fetch remote desktop connection status from Firestore", e);
//				throw new BishopTimeoutException("Unable to get remote desktop connection status");
//			}
//			G.wait.sleep(1);
//		}
//
//		if (connectionFailed) {
//			throw new Exception(
//					String.format("Connection to remote machine '%s' is not established.", remoteDesktopId));
//		} else if (connectionSuccessful) {
//			logger.info("Connection established to the remote machine: " + remoteDesktopId);
//			G.wait.sleep(5);
//			if (lockStatusCheck) {
//				if (isMachineLocked()) {
//					throw new BishopRuleViolationException(String.format(
//							"Unable to unlock the remote desktop %s. Please contact Bishop Support.", remoteDesktopId));
//				} else {
//					logger.info("Remote desktop unlocked successfully");
//				}
//			} else {
//				logger.info("Unlock status check is disabled for the remote desktop");
//			}
//			G.wait.sleep(5);
//		} else {
//			throw new Exception(
//					String.format("Connection to remote machine '%s' is not established.", remoteDesktopId));
//		}
	}

	private void requestToDisconnectRemoteSession(String remoteDesktopId) {
//		try {
//			DocumentSnapshot remoteDesktop = FirestoreDB.db.collection("automation-desktop").document(remoteDesktopId)
//					.get().get();
//			boolean rdpConnection = false;
//			if (remoteDesktop.contains("rdpConnection")) {
//				rdpConnection = remoteDesktop.getBoolean("rdpConnection");
//			}
//
//			if (rdpConnection) {
//				DocumentReference remoteDesktopToUpdate = FirestoreDB.db.collection("automation-desktop")
//						.document(remoteDesktopId);
//				remoteDesktopToUpdate.update("closeRdpConnection", true).get();
//				logger.info(String.format("Request sent to disconnect RDP session for remote machine '%s'",
//						remoteDesktopId));
//			}
//
//		} catch (Exception e) {
//			logger.error(String.format("Unable to send request to disconnect RDP session for remote machine '%s'",
//					remoteDesktopId), e);
//		}
	}

	public String getHostName() throws IOException {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec("hostname").getInputStream()).useDelimiter("\\A")) {
			return s.hasNext() ? s.next().trim().replace("\n", "") : "";
		}
	}

	public boolean isMachineLocked() throws IOException {
		String logonuiProcess = StringUtils.EMPTY;
		try (Scanner s = new Scanner(Runtime.getRuntime()
				.exec("TASKLIST -V /FI \"IMAGENAME eq logonui.exe\" /fi \"SessionName eq Console\"").getInputStream())
				.useDelimiter("\\A")) {
			logonuiProcess = s.hasNext() ? s.next().trim().replace("\n", "") : "";
		}
		if (StringUtils.containsIgnoreCase(logonuiProcess, "LogonUI")) {
			logger.info("Machine is currently locked.");
			return true;
		} else {
			logger.info("Machine is currently unlocked.");
			return false;
		}
	}
}
