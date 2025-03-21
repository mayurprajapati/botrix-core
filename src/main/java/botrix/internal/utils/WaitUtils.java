package botrix.internal.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;

public class WaitUtils {
	private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);

	private WaitUtils() {
	}

	public static void sleepSeconds(long seconds) {
		sleep(Duration.ofSeconds(seconds));
	}

	public static void sleep(Duration timeout) {
		logger.debug("►");
		try {
			if (!timeout.isNegative()) {
				boolean doLog = timeout.toSeconds() > 5;

				if (doLog)
					logger.info("😴 sleeping for {}", DurationUtils.humanReadableFormat(timeout));
				Thread.sleep(timeout.toMillis());

				if (doLog)
					logger.info("🥱 woke up");
			}
		} catch (InterruptedException e) {
		}
		logger.debug("◀");
	}

	public static void main(String[] args) {
		sleep(Duration.ofSeconds(1));
	}

	public static void sleepUntil(LocalDateTime time) {
		sleep(Duration.between(LocalDateTime.now(), time));
	}

	public static class WaitUntil<T> {
		private Duration timeout;
		private Duration pollingTime = Duration.ofMillis(500);
		private Supplier<T> code;
		private Predicate<T> checker;

		private WaitUntil(Supplier<T> code, Predicate<T> checker) {
			this.code = code;
			this.checker = checker;
		}

		public WaitUntil<T> timeout(Duration timeout) {
			this.timeout = timeout;
			return this;
		}

		public WaitUntil<T> polling(Duration pollingTime) {
			this.pollingTime = pollingTime;
			return this;
		}

		public T execute() {
			Validate.notNull(code, "Condition is not set");
			Validate.notNull(timeout, "Timeout is not set");
			Validate.notNull(pollingTime, "Polling time is not set");

			do {
				T result = code.get();
				if (checker.test(result))
					return result;
				sleep(pollingTime);
				timeout = timeout.minus(pollingTime);
			} while (!timeout.isNegative());

			return null;
		}

		public <E extends Throwable> T executeThrowing(Supplier<E> exceptionSupplier) throws E {
			T result = execute();
			if (!checker.test(result)) {
				throw exceptionSupplier.get();
			}

			return result;
		}
	}

	public static WaitUntil<Boolean> waitUntilTrue(Supplier<Boolean> condition) {
		return new WaitUntil<Boolean>(condition, (r) -> r);
	}

	public static <T> WaitUntil<T> waitUntilNotNull(Supplier<T> condition) {
		return new WaitUntil<T>(condition, (r) -> r != null);
	}
}
