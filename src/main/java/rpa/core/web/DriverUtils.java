package rpa.core.web;

import rpa.core.driver.G;

public class DriverUtils {

	public static boolean isWebDriverActive() {
		if (G.driver != null && G.driver.getSessionId() != null) {
			return true;
		}
		return false;
	}

}
