package rpa.core.exceptions;

import lombok.Getter;
import rpa.core.web.Screenshot;

/**
 * Base exception for all Bishop-bot related errors
 * 
 * @author Mayur
 */

public class BishopException extends Exception {
	private static final long serialVersionUID = 926623120565370445L;

	@Getter
	private BishopExceptionCause bishopExceptionCause = BishopExceptionCause.DEFAULT;

	public BishopException() {
		this("");
	}

	public BishopException(String message) {
		this(message, true);
	}

	public BishopException(String message, boolean takeScreenshot) {
		this(message, null, takeScreenshot);
	}

	public BishopException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e);
		if (takeScreenshot)
			Screenshot.take();
	}

	public BishopException setBishopExceptionCause(BishopExceptionCause cause) {
		this.bishopExceptionCause = cause;
		return this;
	}

	/**
	 * Find and return top most exception of type {@link BishopException}.
	 * 
	 * @param e
	 * @return BishopException
	 */
	public static BishopException find(Throwable e) {
		if (e instanceof BishopException) {
			return (BishopException) e;
		}

		return e == null ? null : find(e.getCause());
	}
}
