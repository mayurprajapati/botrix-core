package rpa.core.exceptions;

/**
 * Indicates an related to "Bishop Plugin System".<br>
 * 
 * @see PluginInvoker & Plugins
 * 
 * @author Mayur
 */
public class BishopPluginNotFoundException extends BishopRuleViolationException {
	private static final long serialVersionUID = -1462522422707257474L;

	public BishopPluginNotFoundException() {
		this("");
	}

	public BishopPluginNotFoundException(String message) {
		this(message, null);
	}

	public BishopPluginNotFoundException(String message, Throwable e) {
		this(message, e, false);
	}

	public BishopPluginNotFoundException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
