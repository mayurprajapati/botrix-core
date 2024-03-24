package appium.wrapper.utils;

import java.util.Random;

public class RandomUtils {
	/**
	 * 
	 * @param min inclusive
	 * @param max exclusive
	 * @return
	 */
	public static int randomIntBetween(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min) + min;
	}

	/**
	 * 
	 * @param min inclusive
	 * @param max exclusive
	 * @return
	 */
	public static long randomLongBetween(long min, long max) {
		Random r = new Random();
		return r.nextLong(max - min) + min;
	}

	/**
	 * 
	 * @param min inclusive
	 * @param max exclusive
	 * @return
	 */
	public static double randomDoubleBetween(double min, double max) {
		Random r = new Random();
		return r.nextDouble(min - max) + min;
	}
}
