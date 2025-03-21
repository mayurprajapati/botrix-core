package rpa.core.exceptions;

/**
 * Indicates error while launching an application.
 * 
 * @author Mayur
 */
public class BishopAppLaunchException extends BishopException {
	private static final long serialVersionUID = -3099139647339170166L;

	public BishopAppLaunchException() {
		this("");
	}

	public BishopAppLaunchException(String message) {
		this(message, null);
	}

	public BishopAppLaunchException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopAppLaunchException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
