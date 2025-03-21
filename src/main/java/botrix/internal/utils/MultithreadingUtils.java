package botrix.internal.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadingUtils {
	private MultithreadingUtils() {
	}

	public static <T> List<T> resolve(List<Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return resolve(10, tasks);
	}

	public static <T> List<T> resolve(int threadCount, List<Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<Future<T>> futures = new ArrayList<>();

		futures = executor.invokeAll(tasks);

		executor.shutdownNow();

		List<T> responses = new ArrayList<>();
		for (Future<T> future : futures) {
			responses.add(future.get());
		}

		return responses;
	}
}
