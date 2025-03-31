package rpa.core.exceptions;

/**
 * Indicates error while launching an application.
 * 
 * @author Mayur
 */
public class BotrixAppLaunchException extends BishopException {
	private static final long serialVersionUID = -3099139647339170166L;

	public BotrixAppLaunchException() {
		this("");
	}

	public BotrixAppLaunchException(String message) {
		this(message, null);
	}

	public BotrixAppLaunchException(String message, Throwable e) {
		this(message, e, true);
	}

	public BotrixAppLaunchException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
