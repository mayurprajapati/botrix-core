package rpa.core.exceptions;

/**
 * Indicates an unexpected/unknown state that the framework does not know how to
 * solve. <br>
 * 
 * Also indicates that "Bishop IPA" should not retry current referred record.
 * 
 * @author Mayur
 */
public class BishopRuleViolationNoRetryException extends BishopRuleViolationException {
	private static final long serialVersionUID = -6426445109691015570L;

	public BishopRuleViolationNoRetryException() {
		this("");
	}

	public BishopRuleViolationNoRetryException(String message) {
		this(message, null);
	}

	public BishopRuleViolationNoRetryException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopRuleViolationNoRetryException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
