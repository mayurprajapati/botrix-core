package appium.wrapper.driver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;

import appium.wrapper.locator.AppiumLocator;
import grpc.bridge.python.PythonBridgeClient;
import io.restassured.http.Cookies;
import lombok.SneakyThrows;

public class BrowserDriverWrapper extends AppiumDriverWrapper {
	private BrowserDriverBuilderOptions options;

	public BrowserDriverWrapper(RemoteWebDriver driver, Process applicationProcess,
			BrowserDriverBuilderOptions options) {
		super(driver, applicationProcess);
		this.options = options;
	}

	public boolean isRecaptchaAvailable() {
		return isPresent(AppiumLocator.byXpath("reCAPTCHA", "//iframe[@title=\"reCAPTCHA\"]"));
	}

	public void resolveRecaptcha() {
		URI url = getDevtoolsUri();
		PythonBridgeClient.getInstance().resolveRecaptcha(url.getHost(), url.getPort());
	}

	public void get(String url) {
		driver.get(url);
	}

	public Set<Cookie> getCookies() {
		return driver.manage().getCookies();
	}

	public Cookies getCookiesForRestAssured() {
		List<io.restassured.http.Cookie> restAssuredCookies = getCookies().stream()
				.map(cookie -> new io.restassured.http.Cookie.Builder(cookie.getName(), cookie.getValue())
						.setDomain(cookie.getDomain()).setPath(cookie.getPath()).setSecured(cookie.isSecure())
						.setHttpOnly(cookie.isHttpOnly()).setExpiryDate(cookie.getExpiry()).build())
				.collect(Collectors.toList());
		return new Cookies(restAssuredCookies);
	}

	public Cookie getCookieNamed(String name) {
		return driver.manage().getCookieNamed(name);
	}

	public void addCookie(Cookie cookie) {
		driver.manage().addCookie(cookie);
	}

	public void deleteCookie(Cookie cookie) {
		driver.manage().deleteCookie(cookie);
	}

	public void deleteCookieNamed(String name) {
		driver.manage().deleteCookieNamed(name);
	}

	public void deleteAllCookies() {
		driver.manage().deleteAllCookies();
	}

	@SneakyThrows
	public void quit() {
		super.quit();
		if (!options.isKeepUserDataDir()) {
			for (int i = 0; i < 5; i++) {
				try {
					FileUtils.forceDelete(new File(options.getUserDataDir()));
					return;
				} catch (IOException e) {
					if (StringUtils.containsAnyIgnoreCase(e.getMessage(),
							"DOS or POSIX file operations not available for", "Cannot delete file")) {
						Thread.sleep(100);
					} else if (e.getMessage().contains("File does not exist")) {
						return;
					} else {
						throw e;
					}
				}
			}
		}
	}

}
