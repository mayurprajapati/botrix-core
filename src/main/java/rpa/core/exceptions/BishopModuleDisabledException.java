package rpa.core.exceptions;

/**
 * Indicates error that the module is disabled on the system on which "Bishop Bot"
 * trying to do operations.<br>
 * Generally requires manual intervention. <br>
 * 
 * @author Mayur
 */
public class BishopModuleDisabledException extends BishopException {
	private static final long serialVersionUID = -6481329389187947606L;

	public BishopModuleDisabledException() {
		this("");
	}

	public BishopModuleDisabledException(String message) {
		this(message, null);
	}

	public BishopModuleDisabledException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopModuleDisabledException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
