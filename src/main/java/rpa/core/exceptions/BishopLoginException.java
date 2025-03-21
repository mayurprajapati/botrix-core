package rpa.core.exceptions;

/**
 * Indicates error while logging into the client system. <br>
 * 
 * @author Mayur
 */
public class BishopLoginException extends BishopException {
	private static final long serialVersionUID = -108769729180128061L;

	public BishopLoginException() {
		this("");
	}

	public BishopLoginException(String message) {
		this(message, null);
	}

	public BishopLoginException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopLoginException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
