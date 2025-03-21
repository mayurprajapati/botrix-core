package botrix.commons.time;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import rpa.core.exceptions.BishopRuntimeException;
import rpa.core.utils.DateFormat;

public class DateUtils {
	public static LocalDateTime epochSecondsToDate(long epochSeconds) {
		return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDateTime epochMillisToDate(long epochMillis) {
		return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static Date localDateTimeToDate(LocalDateTime time) {
		return localDateTimeToDate(time, ZoneId.systemDefault());
	}

	public static Date localDateTimeToDate(LocalDateTime time, ZoneId zone) {
		return Date.from(time.atZone(zone).toInstant());
	}

	public static long dateToEpochSeconds(LocalDateTime date) {
		return dateToMillis(date) / 1000;
	}

	public static long dateToMillis(LocalDateTime date) {
		return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static LocalDate localdate(String date, DateFormat format) {
		return LocalDate.parse(date, DateTimeFormatter.ofPattern(format.getFormat()));
	}

	public static LocalDate localDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static boolean isDay(LocalDate date, DayOfWeek day) {
		return date.getDayOfWeek() == day;
	}

	public static boolean isSunday(LocalDate date) {
		return date.getDayOfWeek() == DayOfWeek.SUNDAY;
	}

	public static boolean isSaturday(LocalDate date) {
		return date.getDayOfWeek() == DayOfWeek.SATURDAY;
	}

	public static boolean isToday(LocalDate date) {
		return LocalDate.now().isEqual(date);
	}

	public static Date parse(String date, String format) {
		try {
			org.apache.commons.lang3.time.DateUtils.parseDateStrictly(date, format);
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to parse date '%s' using format '%s'", e);
		}

		return null;
	}
}
