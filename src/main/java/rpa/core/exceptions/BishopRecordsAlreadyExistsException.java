package rpa.core.exceptions;

/**
 * Indicates a state where the record which "Bishop Bot" is trying to create
 * already exist on particular system. <br>
 * 
 * Also this exception tells Bishop IPA to not to retry current referred record.
 * 
 * @author Mayur
 */
public class BishopRecordsAlreadyExistsException extends BishopRecordException {
	private static final long serialVersionUID = 309050656468965638L;

	public BishopRecordsAlreadyExistsException() {
		this("");
	}

	public BishopRecordsAlreadyExistsException(String message) {
		this(message, null);
	}

	public BishopRecordsAlreadyExistsException(String message, Throwable e) {
		this(message, e, true);
	}

	public BishopRecordsAlreadyExistsException(String message, Throwable e, boolean takeScreenshot) {
		super(message, e, takeScreenshot);
	}
}
