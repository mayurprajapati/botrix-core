package rpa.core.plugins;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.annotations.BishopPluginLibrary;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.ParseUtils;
import rpa.core.utils.DateFormat;

@BishopPluginLibrary
public class BishopPlugins {

	// Uninstantiable
	private BishopPlugins() {
	}

	/**
	 * Every plugin method should be static
	 * 
	 * @author Mayur Prajapati
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BishopPlugins.class);

	/**
	 * Get date after evaluating expression given in 'day' param <br>
	 * Example Evals: <br>
	 * today-1 = Current day - 1 day<br>
	 * monday-1 = Current Week's monday - 1 day <br>
	 * monday = Current Week's monday <br>
	 * today = Current day <br>
	 * Format date with 'format' parameter<br>
	 * 
	 * @param map - options
	 * @return result
	 */
	public static String getDateForEval(Map<String, String> map) {
		String dayEval = map.get("dayEval");
		String format = map.get("format");

		// defaults
		if (isBlank(format))
			format = ParseUtils.MMDDYYYY;

		String weekday = EMPTY;
		String operator = EMPTY;
		String operand = EMPTY;
		boolean hasOperator = true;

		if (dayEval.contains("+")) {
			operator = "+";
		} else if (dayEval.contains("-")) {
			operator = "-";
		} else {
			weekday = dayEval;
			operator = "+";
			operand = "0";

			hasOperator = false;
		}

		if (hasOperator) {
			weekday = split(dayEval, operator)[0];
			operand = split(dayEval, operator)[1];
		}

		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);

		if (weekday.equalsIgnoreCase("today")) {
			// calendar is already set for today
		} else {
			c.set(Calendar.DAY_OF_WEEK, getDayIndexForWeekday(weekday));
		}
		c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(operator + operand));

		String parsed = ParseUtils.formatDate(format, c.getTime());
		LOGGER.info("Converted '{}' to '{}'", map, parsed);

		return parsed;
	}

	private static int getDayIndexForWeekday(String weekday) {
		if (equalsIgnoreCase(weekday, "monday"))
			return Calendar.MONDAY;
		if (equalsIgnoreCase(weekday, "tuesday"))
			return Calendar.TUESDAY;
		if (equalsIgnoreCase(weekday, "wednesday"))
			return Calendar.WEDNESDAY;
		if (equalsIgnoreCase(weekday, "thursday"))
			return Calendar.THURSDAY;
		if (equalsIgnoreCase(weekday, "friday"))
			return Calendar.FRIDAY;
		if (equalsIgnoreCase(weekday, "saturday"))
			return Calendar.SATURDAY;
		if (equalsIgnoreCase(weekday, "sunday"))
			return Calendar.SUNDAY;

		throw new IllegalArgumentException("Day not found: " + weekday);
	}

	public static String safeFormatDate(Map<String, String> map, Object payload) throws Exception {
		String toFormat = map.get("toFormat");
		String date = map.get("date");

		return DateFormat.safeFormatDate(date, toFormat);
	}

	/**
	 * Get "value" of "key" from payload
	 * 
	 * @param map     - get the "key" from
	 * @param payload - get the "value" from
	 * @return the value
	 * @throws BishopRuleViolationException
	 */
	public static String payload(Map<String, String> map, Object payload) throws BishopRuleViolationException {
		String key = map.get("key");
		if (!(payload instanceof Map)) {
			throw new BishopRuleViolationException(
					"Additional parameter passed to plugin(payload) should be type of a Map.class");
		}

		@SuppressWarnings("unchecked")
		Map<String, String> mapPayload = (Map<String, String>) payload;

		return mapPayload.get(key);
	}

	public static String keepNumbers(Map<String, String> map) {
		String value = map.get("value");
		return ParseUtils.keepNumbers(value);
	}

	/**
	 * Plugin to get one of the value of given attributes in provided payload. <br>
	 * <br>
	 * Constraint: The key & value must not be blank
	 * 
	 * @param map
	 * @param payload
	 * @return value if key & value non blank else empty string
	 * @throws BishopRuleViolationException
	 */
	@SuppressWarnings("unchecked")
	public static String getOneAvailableValueOf(Map<String, String> map, Object payload)
			throws BishopRuleViolationException {
		List<String> fields = Arrays.asList(map.get("fields").split("\\|"));

		if (!(payload instanceof Map)) {
			throw new BishopRuleViolationException(
					"Additional parameter passed to plugin(getOneAvailableValueOf) should be type of a Map.class");
		}

		Map<String, String> mapPayload = (Map<String, String>) payload;

		String keyThatExists = fields.stream().filter(k -> StringUtils.isNotBlank(mapPayload.get(k))).findFirst()
				.orElse(StringUtils.EMPTY);

		if (StringUtils.isNotBlank(keyThatExists))
			return mapPayload.get(keyThatExists);

		return StringUtils.EMPTY;
	}
}
