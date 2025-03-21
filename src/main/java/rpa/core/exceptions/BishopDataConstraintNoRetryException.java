package rpa.core.exceptions;

/**
 * Indicates error that the "Bishop Client" might want to resolve. <br>
 * Generally requires manual intervention. <br>
 * 
 * Also indicates that "Bishop IPA" should not retry current referred record.
 * 
 * @author Mayur
 */
public class BishopDataConstraintNoRetryException extends BishopDataConstraintException {
	private static final long serialVersionUID = 7896576781375216169L;

	public BishopDataConstraintNoRetryException() {
		this("");
	}

	public BishopDataConstraintNoRetryException(String message) {
		this(message, null);
	}

	public BishopDataConstraintNoRetryException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopDataConstraintNoRetryException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
