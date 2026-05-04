package botrix.utils;

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
		ExecutorService executor = Executors.newFixedThreadPool(threadCount, r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});

		List<Future<T>> futures = executor.invokeAll(tasks);

		executor.shutdownNow();
		try {
			if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
				System.err.println("Executor did not terminate in time");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		List<T> responses = new ArrayList<>();
		for (Future<T> future : futures) {
			responses.add(future.get());
		}

		return responses;
	}
}
