package rpa.core.exceptions;

import lombok.Getter;
import rpa.core.web.Screenshot;

/**
 * Indicates runtime exception in Bishop framework
 * 
 * @author Mayur
 */
public class BishopRuntimeException extends RuntimeException {
	@Getter
	private BishopExceptionCause bishopExceptionCause = BishopExceptionCause.DEFAULT;

	private static final long serialVersionUID = 1264927798333781560L;

	public BishopRuntimeException() {
		this("");
	}

	public BishopRuntimeException(String message) {
		this(message, true);
	}

	public BishopRuntimeException(String message, boolean takeScreenshot) {
		this(message, null);
	}

	public BishopRuntimeException setBishopExceptionCause(BishopExceptionCause cause) {
		this.bishopExceptionCause = cause;
		return this;
	}

	public BishopRuntimeException(String message, Throwable e) {
		super(message, e);
	}

}
