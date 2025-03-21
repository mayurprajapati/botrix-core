package rpa.core.exceptions;

/**
 * Exception for unknown errors. <br>
 * Use this exception to identify unknown errors & must be handled thereafter.
 * 
 * @author Mayur
 */
public class BishopUnknownException extends BishopException {
	private static final long serialVersionUID = -1819235590586732865L;

	public BishopUnknownException() {
		this("");
	}

	public BishopUnknownException(String message) {
		this(message, null);
	}

	public BishopUnknownException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopUnknownException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
