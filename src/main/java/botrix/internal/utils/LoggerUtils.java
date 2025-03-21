package botrix.internal.utils;

import org.apache.commons.lang3.function.FailableRunnable;
import org.slf4j.Logger;

public class LoggerUtils {
	public static <T extends RuntimeException> void withLogging(Logger logger, Runnable code) {
		logger.debug("►");

		try {
			code.run();
		} finally {
			logger.debug("◀");
		}
	}

	public static <T extends Throwable> void withLogging(Logger logger, FailableRunnable<T> code) throws T {
		logger.debug("►");

		try {
			code.run();
		} finally {
			logger.debug("◀");
		}
	}
}
