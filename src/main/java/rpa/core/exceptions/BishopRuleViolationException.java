package rpa.core.exceptions;

/**
 * Indicates an unexpected/unknown state that the framework does not know how to
 * solve. <br>
 * 
 * @author Mayur
 */
public class BishopRuleViolationException extends BishopException {
	private static final long serialVersionUID = -6804572634120756624L;

	public BishopRuleViolationException() {
		this("");
	}

	public BishopRuleViolationException(String message) {
		this(message, null);
	}

	public BishopRuleViolationException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopRuleViolationException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
