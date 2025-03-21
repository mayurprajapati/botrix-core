package rpa.core.exceptions;

/**
 * Indicates error that the "Bishop Client" might want to resolve. <br>
 * Generally requires manual intervention. <br>
 * 
 * Also this exception tells Bishop IPA to not to retry current referred record.
 * 
 * @author Mayur
 */
public class BishopExitWithoutActionException extends BishopException {
	private static final long serialVersionUID = -202294944384721457L;

	public BishopExitWithoutActionException() {
		this("");
	}

	public BishopExitWithoutActionException(String message) {
		this(message, null);
	}

	public BishopExitWithoutActionException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopExitWithoutActionException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
