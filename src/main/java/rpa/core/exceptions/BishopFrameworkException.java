package rpa.core.exceptions;

import lombok.Getter;
import rpa.core.web.Screenshot;

/**
 * Base exception for all framework related errors
 * 
 * @author Mayur
 */

public class BishopFrameworkException extends Exception {
	private static final long serialVersionUID = 926623120565370445L;

	@Getter
	private BishopExceptionCause bishopExceptionCause = BishopExceptionCause.DEFAULT;

	public BishopFrameworkException() {
		this("");
	}

	public BishopFrameworkException(String message) {
		this(message, true);
	}

	public BishopFrameworkException(String message, boolean takeScreenshot) {
		this(message, null, takeScreenshot);
	}

	public BishopFrameworkException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopFrameworkException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e);
		if (takeScreenshot)
			Screenshot.take();
	}

	public BishopFrameworkException setBishopExceptionCause(BishopExceptionCause cause) {
		this.bishopExceptionCause = cause;
		return this;
	}

	/**
	 * Find and return top most exception of type {@link BishopFrameworkException}.
	 * 
	 * @param e
	 * @return BishopFrameworkException
	 */
	public static BishopFrameworkException find(Throwable e) {
		if (e instanceof BishopFrameworkException) {
			return (BishopFrameworkException) e;
		}

		return e == null ? null : find(e.getCause());
	}
}
