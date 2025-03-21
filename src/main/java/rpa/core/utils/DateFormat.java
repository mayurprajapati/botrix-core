package rpa.core.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.ParseUtils;

/**
 * Date formats starting with Month 'M' must come first because MDY is used by
 * USA & Canada mostly https://en.wikipedia.org/wiki/Date_format_by_country <br>
 * then formats starting with Day 'd' <br>
 * then formats starting with Year 'y'
 * 
 * @author Mayur
 */
public enum DateFormat {
	//@formatter:off
	
	MONTH_DATE_YEAR_TIME("MMM dd, yyyy h:mm:ss a", DateFormatType.DateTime),
	MONTH_TIME("MMMM dd, yyyy h:mm a", DateFormatType.DateTime),
	DD_DASH_MMM_DASH_YYYY_SPACE_HH_COLON_MM_COLON_SS("dd-MMM-yyyy HH:mm:ss", DateFormatType.DateTime),
	DD_DASH_MMM_DASH_YYYY_SPACE_HH_COLON_MM("dd-MMM-yyyy HH:mm", DateFormatType.DateTime),
	DDDD_COMMA_MMMM_DD_COMMA_YYYY("dddd, mmmm dd, yyyy", DateFormatType.Date),
	TIMESTAMP("yyyy-MM-dd'T'HH:mm:ss", DateFormatType.DateTime),
	TIMESTAMP_WITH_MS("yyyy-MM-dd'T'HH:mm:ss:SSS", DateFormatType.DateTime),
	YYYY_DASH_MM_DASH_DD_HH_COLON_MM_COLON_SS("yyyy-MM-dd HH:mm:ss", DateFormatType.DateTime),
	Bishop_TIMESTAMP("yyyy-MM-dd'T'hh.mm.ss", DateFormatType.DateTime),
	MMM_SPACE_DD_COMMA_YYYY_COMMA_SPACE_H_MM_SS_AA("MMM dd, yyyy, h:mm:ss aa", DateFormatType.DateTime),
	DayDDMONTimeStampYYYY("E MMM dd HH:mm:ss z yyyy", DateFormatType.DateTime),
	DAY_SPACE_MM_SLASH_DD_SLASH_YYYY("EEE MM/dd/yyyy", DateFormatType.Date),

	// starting with 'M' month
	MMM_yy("MMM-yy", DateFormatType.Date),
	MON_DD("MMM dd", DateFormatType.Date),
	MM_SLASH_YY("MM/yy", DateFormatType.Date),
	MONTH_YEAR("MMMM yyyy", DateFormatType.Date),
	MMDDYYYY("MMddyyyy", DateFormatType.Date),
	MMDDYY("MMddyy", DateFormatType.Date),
	M_DASH_DD_DASH_YYYY("M-dd-yyyy", DateFormatType.Date),
	MM_SLASH_DD_SLASH_YY("MM/dd/yy", DateFormatType.Date),
	M_SLASH_D_SLASH_YYYY("M/d/yyyy", DateFormatType.Date),
	MM_DOT_DD_DOT_YYYY("MM.dd.yyyy", DateFormatType.Date),
	MM_DASH_DD_DASH_YY("MM-dd-yy", DateFormatType.Date),
	MM_DASH_DD_DASH_YYYY("MM-dd-yyyy", DateFormatType.Date),
	MM_SLASH_DD_SLASH_YYYY("MM/dd/yyyy", DateFormatType.Date),
	MMM_DD_COMMA_YYYY("MMM dd, yyyy", DateFormatType.Date),
	MONTH_DATE_COMMA_YEAR("MMMM dd, yyyy", DateFormatType.Date),
	MMM_ddth_yyyy("MMM ddth, yyyy", DateFormatType.Date),
	MMM_DTH_YYYY("MMM dth, yyyy", DateFormatType.Date),
	MMM_dd_yyyy("MMM dd yyyy", DateFormatType.Date),
	MMMM_dd_yyyy("MMMM dd yyyy", DateFormatType.Date),
	MMM_COMMA_DD_YYYY("MMM, dd yyyy", DateFormatType.Date),
	

	// starting with 'd' day
	DDMMYYYY("ddMMyyyy", DateFormatType.Date),
	DD_SLASH_MM_SLASH_YYYY("dd/MM/yyyy", DateFormatType.Date),
	DATE_MONTH_YEAR("dd MMMM yyyy", DateFormatType.Date),
	DD_DASH_MM_DASH_YYYY("dd-MM-yyyy", DateFormatType.Date),
	DD_DASH_MMM_DASH_YYYY("dd-MMM-yyyy", DateFormatType.Date),
	DD_DASH_MMM_DASH_YY("dd-MMM-yy", DateFormatType.Date),
	

	// starting with 'y' year
	YYYYMMDD("yyyyMMdd", DateFormatType.Date),
	YYYY_DASH_MM_DASH_DD("yyyy-MM-dd", DateFormatType.Date),
	YYYY_SLASH_MM_SLASH_DD("yyyy/MM/dd", DateFormatType.Date),
	

	// Misc
	
	HH_COLON_MM_COLON_SS("HH:mm:ss", DateFormatType.Time),
	HH_COLON_MM("HH:mm", DateFormatType.Time);
	//@formatter:on

	public static enum DateFormatType {
		Date, DateTime, Time
	}

	/**
	 * String representation of date format
	 */
	@Getter
	private String format;
	@Getter
	private DateFormatType type;

	private DateFormat(String format, DateFormatType type) {
		this.format = format;
		this.type = type;
	}

	/**
	 * Check if date is parsable with current format
	 * 
	 * @param date
	 * @return true if can parse successfully else false
	 */
	public boolean isParsable(String date) {
		try {
			ParsePosition position = new ParsePosition(0);
			SimpleDateFormat sdf = new SimpleDateFormat(this.format);
			sdf.setLenient(false);
			sdf.parse(date, position);

			if (position.getIndex() != date.length()) {
				throw new ParseException("Remainder not parsed: " + date.substring(position.getIndex()),
						position.getIndex());
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Parse & get date object from given string by formatting with current format
	 * 
	 * @param date date to parse
	 * @return parsed date
	 * @throws ParseException
	 */
	public Date date(String date) throws ParseException {
		return ParseUtils.date(date, this.format);
	}

	/**
	 * Find matching date format for given date
	 * 
	 * @param date find date matching date format for
	 * @return matching date format
	 * @see #findFormat(String, DateFormat)
	 * @throws BishopRuleViolationException if not matching with any of the
	 *                                      available formats
	 */
	public static DateFormat findFormat(String date) throws BishopRuleViolationException {
		return findFormat(date, null);
	}

	/**
	 * Find matching date format for given date
	 * 
	 * @param date find date matching date format for
	 * @param type Type of date format to compare with, Date/DateTime/Time
	 * @return matching date format
	 * @see #findFormat(String)
	 * @throws BishopRuleViolationException if not matching with any of the
	 *                                      available formats & defaultDateFormat is
	 *                                      null
	 */
	public static DateFormat findFormat(String date, DateFormatType type) throws BishopRuleViolationException {
		Optional<DateFormat> optional = Arrays.stream(DateFormat.values())
				.filter((df) -> type == null ? true : type == df.type)//
				.filter(df -> df.isParsable(date)).findFirst();

		return optional.orElseThrow(
				() -> new BishopRuleViolationException(String.format("Date format not found for date '%s'", date)));
	}

	/**
	 * Find matching date format for given date
	 * 
	 * @param date find date matching date format for
	 * @return matching date format
	 * @see #findFormatStr(String, String)
	 * @throws BishopRuleViolationException if matching date format not found
	 */
	public static String findFormatStr(String date) throws BishopRuleViolationException {
		try {
			return findFormat(date).format;
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Date format not found for date '%s'", date), e);
		}
	}

	/**
	 * Find matching date format for given date
	 * 
	 * @param date              find date matching date format for
	 * @param defaultDateFormat value to return if not matching with any of
	 *                          available formats
	 * @return matching date format
	 * @see #findFormatStr(String)
	 */
	public static String findFormatStr(String date, String defaultDateFormat) {
		try {
			return findFormat(date).format;
		} catch (Exception e) {
			return defaultDateFormat;
		}
	}

	/**
	 * Format given date to current selected format
	 * 
	 * @param date to format
	 * @return formatted date string
	 * @see ParseUtils#formatDate(String, Date)
	 */
	public String format(Date date) {
		return ParseUtils.formatDate(format, date);
	}

	/**
	 * Format given date to current selected format
	 * 
	 * @param date to format
	 * @return formatted date string
	 * @see ParseUtils#formatDate(String, Date)
	 */
	public String format(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * Format given date to toFormat from current {@link #format}
	 * 
	 * @param toFormat date format
	 * @param date
	 * @return formatted date string
	 * @see ParseUtils#formatDate(String, String, String)
	 */
	public String formatTo(String toFormat, String date) throws Exception {
		return ParseUtils.formatDate(this.format, toFormat, date);
	}

	/**
	 * Format given date to toFormat from current {@link #format}
	 * 
	 * @param toFormat date format
	 * @param date
	 * @return formatted date string
	 * @see ParseUtils#formatDate(String, String, String)
	 */
	public String formatTo(DateFormat toFormat, String date) throws Exception {
		return ParseUtils.formatDate(this.format, toFormat.format, date);
	}

	/**
	 * 1. Find matching date format for given date <br>
	 * 2. Parse to {@link Date} using matched date format <br>
	 * 3. Parse {@link Date} to given toFormat using default timezone
	 * 
	 * @param date     to format
	 * @param toFormat parse given date to this format
	 * @return formatted date string
	 * @see #safeFormatDate(String, String, String)
	 */
	public static String safeFormatDate(String date, String toFormat) throws Exception {
		return safeFormatDate(date, toFormat, ParseUtils.getDefaultTimezone());
	}

	/**
	 * 1. Find matching date format for given date <br>
	 * 2. Parse to {@link Date} using matched date format <br>
	 * 3. Parse {@link Date} to given toFormat using given timezone
	 * 
	 * @param date     to format
	 * @param toFormat parse given date to this format
	 * @param timezone parse using this timezone
	 * @return formatted date string
	 * @see #safeFormatDate(String, String, String, String)
	 * @throws Exception
	 */
	public static String safeFormatDate(String date, String toFormat, String timezone) throws Exception {
		return safeFormatDate(date, toFormat, timezone, null);
	}

	/**
	 * 1. Find matching date format for given date <br>
	 * 2. Parse to {@link Date} using matched date format <br>
	 * 3. Parse {@link Date} to given toFormat using given timezone
	 * 
	 * @param date       to format
	 * @param toFormat   parse given date to this format
	 * @param timezone   parse using this timezone
	 * @param defaultVal return if dateformat not found for given date
	 * @return formatted date string
	 * @throws Exception
	 */
	public static String safeFormatDate(String date, String toFormat, String timezone, String defaultVal)
			throws Exception {
		String fromDateFormat = findFormatStr(date, defaultVal);

		if (StringUtils.isEmpty(fromDateFormat)) {
			return defaultVal;
		}

		return ParseUtils.formatDate(fromDateFormat, toFormat, date, timezone);
	}

	/**
	 * Converts given {@code date} to {@link LocalDateTime} using current
	 * Account's default time zone
	 * 
	 * @see ParseUtils#getDefaultTimezone()
	 * @param date - date to be converted
	 * @return Instance of {@link LocalDateTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalDateTime localDateTime(String date) {
		return localDateTime(date, ZoneId.of(ParseUtils.getDefaultTimezone()));
	}

	/**
	 * Converts given {@code date} to {@link LocalDateTime}
	 * 
	 * @param date   - date to be converted
	 * @param zoneId - Time zone
	 * @return Instance of {@link LocalDateTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalDateTime localDateTime(String date, ZoneId zoneId) {
		return zonedDateTime(date, zoneId).toLocalDateTime();
	}

	/**
	 * Converts given {@code date} to {@link LocalDate} using current Account's
	 * default time zone
	 * 
	 * @see ParseUtils#getDefaultTimezone()
	 * @param date - date to be converted
	 * @return Instance of {@link LocalDate}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalDate localDate(String date) {
		return localDate(date, ZoneId.of(ParseUtils.getDefaultTimezone()));
	}

	/**
	 * Converts given {@code date} to {@link LocalDate}
	 * 
	 * @param date   - date to be converted
	 * @param zoneId - Time zone
	 * @return Instance of {@link LocalDate}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalDate localDate(String date, ZoneId zoneId) {
		return zonedDateTime(date, zoneId).toLocalDate();
	}

	/**
	 * Converts given {@code date} to {@link LocalTime} using current  Account's
	 * default time zone
	 * 
	 * @see ParseUtils#getDefaultTimezone()
	 * @param date - date to be converted
	 * @return Instance of {@link LocalTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalTime localTime(String date) {
		return localTime(date, ZoneId.of(ParseUtils.getDefaultTimezone()));
	}

	/**
	 * Converts given {@code date} to {@link LocalTime} using current  Account's
	 * default time zone
	 * 
	 * @param date   - date to be converted
	 * @param zoneId - Time zone
	 * @return Instance of {@link LocalTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public LocalTime localTime(String date, ZoneId zoneId) {
		return zonedDateTime(date, zoneId).toLocalTime();
	}

	/**
	 * Converts given {@code date} to {@link ZonedDateTime} using system default
	 * time zone which further can be converted to {@link LocalDateTime},
	 * {@link LocalDate} and {@link LocalTime}
	 * 
	 * @see ParseUtils#getDefaultTimezone()
	 * @param date - date to be converted
	 * @return Instance of {@link ZonedDateTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public ZonedDateTime zonedDateTime(String date) {
		return zonedDateTime(date, ZoneId.systemDefault());
	}

	/**
	 * Converts given {@code date} to {@link ZonedDateTime} which further can be
	 * converted to {@link LocalDateTime}, {@link LocalDate} and {@link LocalTime}
	 * 
	 * @param date   - date to be converted
	 * @param zoneId - Time zone
	 * @return Instance of {@link ZonedDateTime}
	 * @throws ParseException
	 */
	@SneakyThrows
	public ZonedDateTime zonedDateTime(String date, ZoneId zoneId) {
		Date converted = date(date);
		return converted.toInstant().atZone(zoneId);
	}

	@Override
	public String toString() {
		return format;
	}

	public static String cleanDate(String date) {
		if (date == null)
			return null;
		date = StringUtils.trimToEmpty(date);

		if (date.equals("-"))
			return StringUtils.EMPTY;

		return date;
	}
}
