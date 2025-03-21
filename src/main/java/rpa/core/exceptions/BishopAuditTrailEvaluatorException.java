package rpa.core.exceptions;

/**
 * RuntimeException indicating error related to AuditTrailEvaluator
 * 
 * @author Mayur
 */
public class BishopAuditTrailEvaluatorException extends BishopRuntimeException {
	private static final long serialVersionUID = -1960949157105599254L;

	public BishopAuditTrailEvaluatorException() {
		this("");
	}

	public BishopAuditTrailEvaluatorException(String message) {
		this(message, null);
	}

	public BishopAuditTrailEvaluatorException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopAuditTrailEvaluatorException(String message, Throwable e, boolean takeScreenshot) {
//		super(message, e, takeScreenshot);
	}
}
