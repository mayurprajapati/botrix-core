
package rpa.core.file;

import rpa.core.exceptions.BishopRuleViolationException;

/**
 * Utility class to determine OS of the system running code
 * 
 * @author aishvaryakapoor
 *
 */
public class OSValidator {
	private static String OS = System.getProperty("os.name").toLowerCase();

	public static String getOS() throws BishopRuleViolationException {
		String os = null;
		if (isWindows()) {
			os = "Windows";
		} else if (isMac()) {
			os = "Mac OS";
		} else if (isUnix()) {
			os = "Unix";
		} else if (isSolaris()) {
			os = "Solaris";
		} else {
			throw new BishopRuleViolationException("OS not supported");
		}
		return os;
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

}
