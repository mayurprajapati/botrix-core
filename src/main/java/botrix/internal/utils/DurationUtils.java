package botrix.internal.utils;

import java.time.Duration;

public class DurationUtils {
	public static String humanReadableFormat(Duration duration) {
		return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
	}

	public static boolean gt(Duration current, Duration other) {
		return current.compareTo(other) > 0;
	}

	public static boolean lt(Duration current, Duration other) {
		return current.compareTo(other) < 0;
	}

	public static boolean gte(Duration current, Duration other) {
		return current.compareTo(other) >= 0;
	}

	public static boolean lte(Duration current, Duration other) {
		return current.compareTo(other) <= 0;
	}
}
