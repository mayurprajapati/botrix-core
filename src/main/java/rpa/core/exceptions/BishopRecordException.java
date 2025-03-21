package rpa.core.exceptions;

/**
 * Indicates an error while doing operations with current referred record.
 * 
 * @author Mayur
 */
public class BishopRecordException extends BishopException {
	private static final long serialVersionUID = 4893068940684816589L;

	public BishopRecordException() {
		this("");
	}

	public BishopRecordException(String message) {
		this(message, null);
	}

	public BishopRecordException(String message, Throwable e) {
		this(message, e, false);
	}

	public BishopRecordException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
