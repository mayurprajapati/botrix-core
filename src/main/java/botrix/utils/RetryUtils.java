package botrix.utils;

import java.util.Optional;

import org.apache.commons.lang3.function.FailableCallable;
import org.apache.commons.lang3.function.FailableRunnable;

import lombok.Getter;
import lombok.Setter;

public class RetryUtils {
	private RetryUtils() {
	}

	@Getter
	@Setter
	public static class BaseResult<E extends Throwable> {
		private Optional<E> exception = Optional.empty();

		public boolean hasError() {
			return exception.isPresent();
		}
	}

	@Getter
	@Setter
	public static class VoidResult<E extends Throwable> extends BaseResult<E> {
		public boolean isSucceed() {
			return !hasError();
		}
	}

	@Getter
	@Setter
	public static class Result<R, E extends Throwable> extends BaseResult<E> {
		private Optional<R> result = Optional.empty();

		public boolean hasResult() {
			return result.isPresent();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <R, E extends Throwable> BaseResult retryAndGetResult(Object code, int retryCount) {
		if (retryCount <= 0)
			throw new RuntimeException("Retry count is <= 0");

		if (!(code instanceof FailableCallable || code instanceof FailableRunnable)) {
			throw new RuntimeException("Code should be an instance of FailableCallable or FailableRunnable");
		}

		E ex = null;
		while (retryCount > 0) {
			try {
				if (code instanceof FailableCallable) {
					Object r = ((FailableCallable) code).call();
					Result<R, E> res = new Result<>();
					res.setException(Optional.empty());
					res.setResult(Optional.ofNullable((R) r));
					return res;
				} else if (code instanceof FailableRunnable) {
					((FailableRunnable) code).run();
					VoidResult<E> res = new VoidResult<>();
					res.setException(Optional.empty());
					return res;
				}
			} catch (Throwable e) {
				ex = (E) e;
			}
			--retryCount;
		}

		if (code instanceof FailableCallable) {
			Result<R, E> res = new Result<>();
			res.setException(Optional.of(ex));
			return res;
		} else if (code instanceof FailableRunnable) {
			VoidResult<E> res = new VoidResult<>();
			res.setException(Optional.of(ex));
			return res;
		}
		throw new IllegalStateException();
	}

	/**
	 * Run code until it succeeds {@code retryCount} times and throw error if
	 * available or else don't do anything
	 * 
	 * @param <E>        Type of result
	 * @param code       code block to run
	 * @param retryCount
	 * @throws E
	 */
	public static <E extends Throwable> void retryGetVoidOrThrowException(FailableRunnable<E> code, int retryCount)
			throws E {
		@SuppressWarnings("unchecked")
		VoidResult<E> res = (VoidResult<E>) retryAndGetResult(code, retryCount);
		if (res.hasError()) {
			throw res.getException().get();
		}
	}

	/**
	 * Run code until it succeeds {@code retryCount} times and return result if
	 * available or throw error
	 * 
	 * @param <R>        Type of result
	 * @param <E>        Type of error
	 * @param code       code block to run
	 * @param retryCount
	 * @return Expected result
	 * @throws E
	 */
	public static <R, E extends Throwable> R retryGetResultOrThrowException(FailableCallable<R, E> code, int retryCount)
			throws E {
		@SuppressWarnings("unchecked")
		Result<R, E> res = (Result<R, E>) retryAndGetResult(code, retryCount);
		if (res.hasError()) {
			throw res.getException().get();
		}
		return res.result.get();
	}
}
