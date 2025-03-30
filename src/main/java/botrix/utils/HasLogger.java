package botrix.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HasLogger {
	public final Logger LOGGER = LoggerFactory.getLogger(getCallerClassName());

	private static String getCallerClassName() {
		return Thread.currentThread().getStackTrace()[3].getClassName();
	}
}
