package botrix.commons.math;

import java.math.BigDecimal;

public class MathUtils {

	private MathUtils() {
	}

	/**
	 * How much percent more does "{@code after}" have?
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double differenceInPercentage(double before, double after) {
		return (after - before) * 100 / before;
	}

	/**
	 * Make {@code value} multiple of {@code multipleOf} by subtracting the
	 * remainder
	 * 
	 * <pre>
	 * makeMultipleOf(10.26843, 0.05) = 10.25
	 * makeMultipleOf(10.25, 0.05) = 10.25
	 * makeMultipleOf(10.25, 1) = 10.0
	 * makeMultipleOf(-10.26843, 0.05) = -10.25
	 * </pre>
	 * 
	 * @param value      - value to round off
	 * @param multipleOf - value to subtract from the {@code value}
	 * @return {@code value} multiple of {@code multipleOf}
	 */
	public static double makeMultipleOf(double value, double multipleOf) {
		BigDecimal p = new BigDecimal(value + "");
		p = p.subtract(p.remainder(new BigDecimal(multipleOf + "")));
		return p.doubleValue();
	}

	public static boolean isMultipleOf(double number, double other) {
		return number % other == 0;
	}

	public static boolean isMultipleOf(long number, long other) {
		return isMultipleOf((double) number, (double) other);
	}

	public static boolean isMultipleOf(int number, int other) {
		return isMultipleOf((double) number, (double) other);
	}

	public static double plusPercentage(double value, double percentage) {
		return value + percentage(value, percentage);
	}

	/**
	 * 
	 * <pre>
	 * percentage(100, 10) = 10.0
	 * percentage(100, 0) = 0.0
	 * </pre>
	 * 
	 * @param value
	 * @param percentage
	 * @return
	 */
	public static double percentage(double value, double percentage) {
		return value * percentage / 100;
	}

	public static void main(String[] args) {
		System.out.println(differenceInPercentage(1868.9, 1838.25));
	}
}
