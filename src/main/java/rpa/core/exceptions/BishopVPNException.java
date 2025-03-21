package rpa.core.exceptions;

/**
 * Indicates VPN related issues.
 * 
 * @author Bishop
 */
public class BishopVPNException extends BishopException {

	private static final long serialVersionUID = -3395436359162796798L;

	public BishopVPNException() {
		this("");
	}

	public BishopVPNException(String message) {
		this(message, null);
	}

	public BishopVPNException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopVPNException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
