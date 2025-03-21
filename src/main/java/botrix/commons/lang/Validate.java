package botrix.commons.lang;

import java.util.Collection;
import java.util.function.Supplier;

public class Validate extends org.apache.commons.lang3.Validate {
	public static <E extends Throwable> void isTrue(final boolean expression, Supplier<E> exceptionToThrow) throws E {
		try {
			isTrue(expression);
		} catch (Throwable ignored) {
			throw exceptionToThrow.get();
		}
	}

	public static <T, E extends Throwable> T notNull(final T object, Supplier<E> exceptionToThrow) throws E {
		try {
			return notNull(object);
		} catch (Throwable ignored) {
			throw exceptionToThrow.get();
		}
	}

	public static <T, E extends Throwable> T[] notEmpty(final T[] array, Supplier<E> exceptionToThrow) throws E {
		try {
			return notEmpty(array);
		} catch (Throwable ignored) {
			throw exceptionToThrow.get();
		}
	}

	public static <T extends Collection<?>, E extends Throwable> T notEmpty(final T collection,
			Supplier<E> exceptionToThrow) throws E {
		try {
			return notEmpty(collection);
		} catch (Throwable ignored) {
			throw exceptionToThrow.get();
		}
	}
}
