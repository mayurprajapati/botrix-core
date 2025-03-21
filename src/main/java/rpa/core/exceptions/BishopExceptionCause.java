package rpa.core.exceptions;

import lombok.Getter;

public enum BishopExceptionCause {
	DEFAULT(""), //
	UNEXPECTED_ALERT("Unexpected & unhandled alert."),
	Bishop_RECORD_ALREADY_LOCKED_OR_CREATED("Bishop record document already locked or created by other run");

	/**
	 * String representation cause
	 */
	@Getter
	private String msg;

	private BishopExceptionCause(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
}
