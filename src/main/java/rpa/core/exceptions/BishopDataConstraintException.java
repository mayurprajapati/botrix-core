package rpa.core.exceptions;

/**
 * Indicates error that the "Bishop Client" might want to resolve. <br>
 * Generally requires manual intervention.
 * 
 * @author Mayur
 */
public class BishopDataConstraintException extends BishopException {
	private static final long serialVersionUID = 4540375036918938838L;

	public BishopDataConstraintException() {
		this("");
	}

	public BishopDataConstraintException(String message) {
		this(message, null);
	}

	public BishopDataConstraintException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopDataConstraintException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
