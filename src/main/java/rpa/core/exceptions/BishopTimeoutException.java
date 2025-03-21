package rpa.core.exceptions;

/**
 * Indicates a timeout error while performing certain operations on client
 * system.
 * 
 * @author Mayur
 */
public class BishopTimeoutException extends BishopException {
	private static final long serialVersionUID = -1373344837311127127L;

	public BishopTimeoutException() {
		this("");
	}

	public BishopTimeoutException(String message) {
		this(message, null);
	}

	public BishopTimeoutException(String message, Throwable e) {
		this(message, e, false);
	}

	public BishopTimeoutException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
