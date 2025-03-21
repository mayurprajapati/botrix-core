package rpa.core.file;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.text.MaskFormatter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.entities.AttributeTemplate;
import rpa.core.excel.CSVReader;
import rpa.core.exceptions.BishopRuleViolationException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Utility class to perform parsing operations related to date, amount values,
 * name field, hashing, text cleaning
 * 
 * @author aishvaryakapoor
 *
 */
public class ParseUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(ParseUtils.class);
	public static final String MMDDYYYY = "MMddyyyy";
	public static final String MM_SLASH_DD_SLASH_YY = "MM/dd/yy";
	public static final String MM_DOT_DD_DOT_YYYY = "MM.dd.yyyy";
	public static final String DD_DASH_MM_DASH_YYYY = "dd-MM-yyyy";
	public static final String MM_DASH_DD_DASH_YY = "MM-dd-yy";
	public static final String DD_MONTH_YYYY = "dd-MMM-yyyy";
	public static final String MM_SLASH_DD_SLASH_YYYY = "MM/dd/yyyy";
	public static final String TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String TIMESTAMP_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss:SSS";
	public static final String MONTH_DATE_YEAR_TIME = "MMM dd, yyyy h:mm:ss a";
	public static final String YYYY_DASH_MM_DASH_DD = "yyyy-MM-dd";
	public static final String MONTH_TIME = "MMMM dd, yyyy h:mm a";
	public static final String MONTH_DATE_COMMA_YEAR = "MMMM dd, yyyy";
	public static final String MON_DATE_COMMA_YEAR = "MMM dd, yyyy";
	public static final String DATE_MONTH_YEAR = "dd MMMM yyyy";
	public static final String MONTH_YEAR = "MMMM yyyy";
	public static final String MON_DD = "MMM dd";
	public static final String MON_DD_YYYY = "MMM dd, yyyy";
	public static final String YYYY = "yyyy";
	public static final String MON = "MMM";
	public static final String YY = "yy";
	public static final String Bishop_TIMESTAMP = "yyyy-MM-dd'T'hh.mm.ss";
	public static final String MMDDYY = "MMddyy";
	public static final String DDMMYYYY = "ddMMyyyy";
	public static final String DayDDMONTimeStampYYYY = "E MMM dd HH:mm:ss z yyyy";
	public static final String MM_SLASH_YY = "MM/yy";
	public static final String DAY_SPACE_MM_SLASH_DD_SLASH_YYYY = "EEE MM/dd/yyyy";
	public static final String DDDD_COMMA_MMMM_DD_COMMA_YYYY = "dddd, mmmm dd, yyyy";
	public static final String DD_SLASH_MM_SLASH_YYYY = "dd/MM/yyyy";
	public static final String MMM_ddth_yyyy = "MMM ddth, yyyy";
	public static final String MMM_dth_yyyy = "MMM dth, yyyy";
	public static final String MMM_DD_YYYY = "MMM dd yyyy";
	public static final String MMMM_DD_YYYY = "MMMM dd yyyy";

	public static final List<String> ALL_DATE_FORMATS = java.util.Arrays.asList(MMDDYYYY, MM_SLASH_DD_SLASH_YY,
			DD_MONTH_YYYY, MM_SLASH_DD_SLASH_YYYY, TIMESTAMP, MONTH_DATE_YEAR_TIME, YYYY_DASH_MM_DASH_DD, MONTH_TIME,
			MON_DD, MON_DD_YYYY, YYYY, MMDDYY, DayDDMONTimeStampYYYY, DAY_SPACE_MM_SLASH_DD_SLASH_YYYY,
			DD_SLASH_MM_SLASH_YYYY, MMM_ddth_yyyy, MMM_dth_yyyy, MMM_DD_YYYY, MMMM_DD_YYYY);

	/**
	 * This method return alphanumeric string
	 * 
	 * @param noOfChars
	 * @return
	 */
	public static String randomAlphaNumberic(int noOfChars) {
		return RandomStringUtils.randomAlphanumeric(noOfChars);
	}

//	public static final List<String> STANDARD_DATE_FORMATS = java.util.Arrays.asList(
//			MM_SLASH_DD_SLASH_YY,
//			MM_DASH_DD_DASH_YY, 
//			"MM.dd.yy", 
//			"dd-MMM-yy", 
//			"dd MMM yy", 
//			MM_SLASH_DD_SLASH_YYYY, 
//			"MM-dd-yyyy", 
//			MM_DOT_DD_DOT_YYYY,
//			YYYY_DASH_MM_DASH_DD, 
//			"yyyy.MM.dd", 
//			"MMMM dd, yyyy", 
//			"MM-dd-yyyy HH:mm:ss",
//			"MM/dd/yy at HH:mm a",
//			DAY_SPACE_MM_SLASH_DD_SLASH_YYYY);

	/**
	 * Creates a md5 hash upto 10 chars
	 * 
	 * @param keyword
	 * @return
	 */
	public static String md5Hash_10(String keyword) {
		return StringUtils.substring(md5Hash(keyword), 0, 10);
	}

	/**
	 * Creates a md5 hash
	 * 
	 * @param keyword
	 * @return
	 */
	public static String md5Hash(String keyword) {
		MessageDigest md = null;
		StringBuffer sb = new StringBuffer();
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(keyword.getBytes());
			byte byteData[] = md.digest();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 10).substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
		}
		return sb.toString();
	}

	public static String getDefaultTimezone() {
		String defaultTimezone = "";
		if (G.executionMetrics != null && G.executionMetrics.getCompany() != null
				&& G.executionMetrics.getCompany().getTimezone() != null) {
			defaultTimezone = G.executionMetrics.getCompany().getTimezone();
		}
		if (StringUtils.isBlank(defaultTimezone)) {
			defaultTimezone = "UTC";
		}
		return defaultTimezone;
	}

	public static String formatDate(String toFormat, Date date) {
		return formatDate(toFormat, date, getDefaultTimezone());
	}

	/**
	 * Format a @java.util.Date object to the provide data format
	 * 
	 * @param toFormat
	 * @param date
	 * @return
	 */
	public static String formatDate(String toFormat, Date date, String timezone) {
		try {
			if (date != null) {
				SimpleDateFormat toSdf = new SimpleDateFormat(toFormat);
				if (StringUtils.isNotBlank(timezone)) {
					toSdf.setTimeZone(TimeZone.getTimeZone(timezone));
				}
				return toSdf.format(date);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to format date " + date, e);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Format a text date to @param toFormat by indetifying the current date format
	 * based on available date formats in @ALL_DATE_FORMATS
	 * 
	 * @param toFormat
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String safeFormatDate(String toFormat, String date) throws Exception {
		return safeFormatDate(toFormat, date, false, getDefaultTimezone());
	}

	/**
	 * Format a text date to @param toFormat by indetifying the current date format
	 * based on available date formats in @ALL_DATE_FORMATS
	 * 
	 * @param toFormat
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String safeFormatDate(String toFormat, String date, boolean throwEx) throws Exception {
		return safeFormatDate(toFormat, date, throwEx, getDefaultTimezone());
	}

	public static String safeFormatDate(String toFormat, String date, boolean throwEx, String timezone)
			throws Exception {
		String identifiedFormat = "";
		if (StringUtils.isBlank(date))
			return StringUtils.EMPTY;
		try {
//			for (String df : BishopCashDateFormats.STANDARD_DATE_FORMATS) {
//				SimpleDateFormat sdf = new SimpleDateFormat(df);
//				try {
//					sdf.parse(date);
//					identifiedFormat = df;
//					break;
//				} catch (Exception e) {
//
//				}
//			}
			if (StringUtils.isBlank(identifiedFormat))
				throw new BishopRuleViolationException(String.format("Date not parsable: %s", date));
			return formatDate(identifiedFormat, toFormat, date, timezone);
		} catch (Exception e) {
			if (!e.getClass().equals(BishopRuleViolationException.class))
				LOGGER.error("", e);
			if (throwEx)
				throw new BishopRuleViolationException(String.format("Date not parsable: %s", date));
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Format a text date from @param fromFormat to @param toFormat
	 * 
	 * @param fromFormat
	 * @param toFormat
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(String fromFormat, String toFormat, String date) throws Exception {
		return formatDate(fromFormat, toFormat, date, getDefaultTimezone());
	}

	/**
	 * Format a text date from @param fromFormat to @param toFormat
	 * 
	 * @param fromFormat
	 * @param toFormat
	 * @param date
	 * @param timeZone
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(String fromFormat, String toFormat, String date, String timeZone) throws Exception {
		if (StringUtils.isNotBlank(date)) {
			SimpleDateFormat fromSdf = new SimpleDateFormat(fromFormat, Locale.ENGLISH);
			SimpleDateFormat toSdf = new SimpleDateFormat(toFormat);
			if (StringUtils.isNotBlank(timeZone)) {
				fromSdf.setTimeZone(TimeZone.getTimeZone(timeZone));
				toSdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			}
			try {
				fromSdf.setLenient(false);
				Date fromDate = fromSdf.parse(date);
				return toSdf.format(fromDate);
			} catch (ParseException e) {
				throw new Exception(String.format("'%s' is not a valid date (%s)", date, fromFormat));
			}
		} else {
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Removes all chars from a string except for alphabets (A-Z and a-z) and
	 * numbers (0-9) and trims the results for spaces
	 * 
	 * @param text
	 * @return
	 */
	public static String keepAlphabetsAndNumbersTrimmed(String text) {
		if (text != null) {
			return StringUtils.trimToEmpty(text.replaceAll("[^a-zA-Z0-9]", StringUtils.EMPTY));
		} else
			return StringUtils.trimToEmpty(text);
	}

	/**
	 * Determines a safe SQL table name from a text value
	 * 
	 * @param text
	 * @return
	 */
	public static String tableName(String text) {
		if (text != null) {
			return text.toLowerCase().replaceAll("[^a-z0-9_]", "_");
		} else
			return text;
	}

	/**
	 * Removes all chars from a string except for alphabets (A-Z and a-z) and
	 * decmals
	 * 
	 * @param text
	 * @return
	 */
	public static String keepAlphabetsAndDecimals(String text) {
		if (text != null) {
			return text.replaceAll("[^a-zA-Z0-9\\.]", StringUtils.EMPTY);
		} else
			return text;
	}

	/**
	 * Removes all chars from a string except for alphabets (A-Z and a-z) and
	 * numbers (0-9)
	 * 
	 * @param text
	 * @return
	 */
	public static String keepAlphabetsAndNumbers(String text) {
		if (text != null) {
			return text.replaceAll("[^a-zA-Z0-9]", StringUtils.EMPTY);
		} else
			return text;
	}

	/**
	 * Removes all chars from a string except for alphabets (A-Z and a-z) and trims
	 * result
	 * 
	 * @param text
	 * @return
	 */
	public static String keepAlphabetsTrimmed(String text) {
		if (text != null) {
			return StringUtils.trim(keepAlphabets(text));
		} else
			return text;
	}

	/**
	 * Removes all chars from a string except for alphabets (A-Z and a-z)
	 * 
	 * @param text
	 * @return
	 */
	public static String keepAlphabets(String text) {
		if (text != null) {
			return text.replaceAll("[^a-zA-Z ]", StringUtils.EMPTY);
		} else
			return text;
	}

	/**
	 * Return a float value from a text
	 * 
	 * @param text
	 * @return
	 */
	public static Float getFloat(String text) {
		if (StringUtils.isBlank(text))
			return 0.0f;
		else
			return Float.valueOf(text);
	}

	/**
	 * Removes all chars from a string decimals values
	 * 
	 * @param text
	 * @return
	 */
	public static String keepDecimals(String text) {
		if (StringUtils.isBlank(text) && StringUtils.equals(text, "null")) {
			return StringUtils.EMPTY;
		}
		String decimalText = "";
		if (text != null) {
			decimalText = StringUtils.trim(text.replaceAll("[^0-9.()\\-]", StringUtils.EMPTY));
		}
		if (StringUtils.startsWith(decimalText, "(") && StringUtils.endsWith(decimalText, "")) {
			decimalText = "-" + StringUtils.trim(decimalText.replaceAll("[^0-9.]", StringUtils.EMPTY));
		}
		return decimalText;
	}

	/**
	 * Removes all chars from a string except for numbers
	 * 
	 * @param text
	 * @return
	 */
	public static String keepNumbers(String text) {
		if (text != null) {
			return text.replaceAll("\\D", StringUtils.EMPTY);
		} else
			return StringUtils.EMPTY;
	}

	/**
	 * Removes all chars from a string except for decimals
	 * 
	 * @param text
	 * @return
	 */
	public static String getDollarAmount(String text) {
		return keepDecimals(text);
	}

	/**
	 * Converts an object to a hasmap representation
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String, String> objectToMap(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		if (object != null) {
			return mapper.convertValue(object, Map.class);
		} else {
			LOGGER.error("Cannot convert null map");
			return new HashMap<String, String>();
		}
	}

	/**
	 * Converts an object to a string representation of hashmap
	 * 
	 * @param text
	 * @return
	 */
	public static String objectToMapString(Object object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (object != null) {
				return String.valueOf(mapper.convertValue(object, Map.class));
			} else {
				return StringUtils.EMPTY;
			}
		} catch (Exception e) {
			LOGGER.error("Failed to convert object to map.", e);
			return String.valueOf(object);
		}
	}

	/**
	 * Converts a map object to a json
	 * 
	 * @param text
	 * @return
	 */
	public static String mapToJson(Map object) {
		if (object != null) {
			try {
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				return gsonObject.toJson(object);
			} catch (Exception e) {
				LOGGER.error("Failed to convert map to json", e);
				return objectToMapString(object);
			}
		} else {
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Converts an object object to a json
	 * 
	 * @param text
	 * @return
	 */
	public static String objectToJson(Object object) {
		if (object != null) {
			try {
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				return gsonObject.toJson(object);
			} catch (Exception e) {
				LOGGER.error("Failed to convert map to json", e);
				throw e;
			}
		} else {
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Cleans text and remove anything inside parentheis including brackets Examples
	 * - "Aish Kapoor (Bishop)" will return "Aish Kapoor"
	 * 
	 * @param text
	 * @return
	 */
	public static String removeStringInParanthesis(String name) {
		if (StringUtils.isBlank(name))
			return StringUtils.EMPTY;
		String pattern = "\\(([^)]+)\\)";
		String onlyName = name.replaceAll(pattern, "");
		return StringUtils.trimToEmpty(onlyName);
	}

	/**
	 * Cleans text and returns the value inside parenthesis Examples - "Aish Kapoor
	 * (Bishop)" will return "Bishop"
	 * 
	 * @param text
	 * @return
	 */
	public static String keepStringInParanthesis(String name) {
		if (StringUtils.isBlank(name))
			return StringUtils.EMPTY;
		String pattern = "\\(([^)]+)\\)";
		return StringUtils.trimToEmpty(evalRegexAndReturn(pattern, name, 1));
	}

	/**
	 * Converts camel case formatted string to readable text Example -
	 * quick_brown_fox will return Quick brown fox
	 * 
	 * @param text
	 * @return
	 */
	public static String camelToSentence(String text) {
		return StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(text), " "));
	}

	/**
	 * Converts hashmap to readable text Example - {firstName=aish, company=Bishop,
	 * lastName=kapoor} will return the following First Name = Aish Company = Bishop
	 * Last Name = Kapoor
	 * 
	 * @param text
	 * @return
	 */
	public static String mapToSentenceString(Map<String, String> map) {
		if (MapUtils.isNotEmpty(map)) {
			try {
				StringBuilder s = new StringBuilder();

				List<String> attributeTemplate = new ArrayList<String>(
						new ObjectMapper().convertValue(new AttributeTemplate(), Map.class).keySet());
				attributeTemplate.add("attachmentDirectory");
				for (String key : map.keySet()) {
					if (!attributeTemplate.contains(key)) {
						String value = String.valueOf(map.get(key));
						if (value == null || "null".equals(value)) {
							value = StringUtils.EMPTY;
						} else {
							value = StringUtils.trimToEmpty(value);
						}
						s.append(camelToSentence(key)).append(": ").append(value).append(System.lineSeparator());
					}
				}
				return s.toString();
			} catch (Exception e) {
				LOGGER.error("Failed to convert map to sentence.", e);
				return mapToJson(map);
			}
		} else {
			return StringUtils.EMPTY;
		}
	}

	public static List<Map> normHeadersForFoundry(List<Map> list) {
		List<Map> allCurrentRecordsNormalisedHeaders = new ArrayList<>();
		for (Map map : list) {
			Map m = new HashMap<>();
			for (Object key : map.keySet()) {
				m.put(normHeaderForFoundry(String.valueOf(key)), map.get(key));
			}
			allCurrentRecordsNormalisedHeaders.add(m);
		}
		return allCurrentRecordsNormalisedHeaders;

	}

	public static String normHeaderForFoundry(String text) {
		return StringUtils.replace(replaceSpecialCharsForFoundry(text), ".", StringUtils.EMPTY);
	}

	public static String replaceSpecialCharsForFoundry(String text) {
		return StringUtils.trimToEmpty(text).replaceAll(
				"[^\\w\\s\\t\\n\\r:;<>=?@\\[\\]\\^_`{}|~\\/.,\\-+\\(\\)*'&%#$\\\"!\\\\]", StringUtils.EMPTY);
	}

	/**
	 * Return date object for date string
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date date(String date, String dateFormat) throws ParseException {
		return date(date, dateFormat, getDefaultTimezone());
	}

	public static Date date(String date, String dateFormat, String timezone) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		if (StringUtils.isNotBlank(timezone)) {
			sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		return sdf.parse(date);
	}

	/**
	 * Removes duplicates from a list
	 * 
	 * @param list
	 * @return
	 */
	public static List<Map> removeDupsFromList(List<Map> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Returns current instant time for @DateTimeZone.UTC and date
	 * format @Bishop_TIMESTAMP
	 * 
	 * @return
	 */
	public static String now() {
		return now(Bishop_TIMESTAMP, getDefaultTimezone());
	}

	/**
	 * Return current instant time in PST
	 * 
	 * @param dateformat
	 * @return
	 */
	public static String now(String dateformat) {
		return now(dateformat, getDefaultTimezone());
	}

	/**
	 * Return current instant time for dateformat and timeZone (e.g. PST, EST, IST)
	 * 
	 * @param dateformat
	 * @param timeZone
	 * @return
	 */
	public static String now(String dateformat, String timeZone) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(dateformat);
		DateTime dt = null;
		if (StringUtils.isNotBlank(timeZone)) {
			dt = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));
		} else {
			dt = DateTime.now();
		}
		String dateTime = dt.toString(dtf);
		return dateTime;
	}

	/**
	 * Round the text decimal to 2 decimal places To be used for parsing dollar
	 * values correcetly
	 * 
	 * @param totalAmmount
	 * @return
	 */
	public static String roundTwoDecimals(String totalAmmount) {
		if (StringUtils.isBlank(totalAmmount)) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(Double.parseDouble(df.format(Double.valueOf(totalAmmount))));
	}

	/**
	 * @param exp  - regular expression
	 * @param text - text to dig in
	 * @param i    - group no
	 * @return evaluated text
	 */
	public static String evalRegexAndReturn(String exp, String text, int i) {
		final Pattern pattern = Pattern.compile(exp, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		String result = "";
		while (matcher.find()) {
			result = matcher.group(i);
		}
		return result.trim();
	}

	/**
	 * Trims to empty an object after checking null
	 * 
	 * @param o
	 * @return blank string if null other trims the object string representation
	 */
	public static String trimToEmpty(Object o) {
		if (o == null)
			return StringUtils.EMPTY;
		return StringUtils.trim(String.valueOf(o));
	}

	/**
	 * Trims to empty an object after checking null else returns Object
	 * 
	 * @param o
	 * @return blank string if null other trims the object string representation
	 */
	public static Object trimToEmptyObject(Object o) {
		if (Objects.isNull(o)) {
			return StringUtils.EMPTY;
		}
		return o;
	}

	/**
	 * Returns a name array based on the input of the @fullName Array has firsname,
	 * middle name and last name
	 * 
	 * @param fullName
	 * @return
	 */
	public static String[] getFirstAndLastName(String fullName) {
		if (StringUtils.isBlank(fullName))
			return ArrayUtils.EMPTY_STRING_ARRAY;
		String[] name = StringUtils.split(fullName);
		String firstName = fullName;
		String lastName = StringUtils.EMPTY;
		String middle = StringUtils.EMPTY;
		if (CollectionUtils.size(name) > 0) {
			firstName = name[0];
			if (CollectionUtils.size(name) > 1) {
				lastName = name[CollectionUtils.size(name) - 1];
			}
			if (CollectionUtils.size(name) > 2) {
				middle = name[CollectionUtils.size(name) - 2];
			}
		}
		String[] nameSplit = new String[3];
		nameSplit[0] = firstName;
		nameSplit[1] = middle;
		nameSplit[2] = lastName;
		return nameSplit;
	}

	public static String cleanStrForPath(String s) {
		return StringUtils.trimToEmpty(
				StringUtils.trimToEmpty(s).replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]", ""));
	}

	/**
	 * Formats the input to a valid dollar figure upto 2 decimal places. e.g. 12.12
	 * 
	 * @param amt
	 * @return
	 */
	public static String formatDollar(String amt) {
		if (StringUtils.isBlank(amt))
			return StringUtils.EMPTY;
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(Double.valueOf(amt));
	}

	/**
	 * Cleans space representated by \u00a0
	 * 
	 * @param str
	 * @return
	 */
	public static String cleanSpaces(String str) {
		if (StringUtils.isBlank(str))
			return StringUtils.EMPTY;
		str = StringUtils.trim(str.replaceAll("\u00a0", " "));
		return str;
	}

	/**
	 * Cleans a string of html tags
	 * 
	 * @param text
	 * @return html free text
	 */
	public static String removeHtml(String text) {
		return Jsoup.parse(text).text();
//			return StringUtils.replace(StringUtils.replace(text, "<p>", ""), "</p>", "");
	}

	public static String checkAndRemoveHTML(String text) {
		return ParseUtils.evalRegexAndReturn(text, "<(\"[^\"]*\"|'[^']*'|[^'\">])*>", 0) != null ? removeHtml(text)
				: text;
	}

	/**
	 * Formats name as LastName, FirstName
	 * 
	 * @param fullName
	 * @return
	 */

	public static String formatName(String fullName) {
		if (StringUtils.contains(fullName, ","))
			return fullName;
		String[] nameArr = getFirstAndLastName(fullName);
		return String.format("%s, %s", nameArr[2], nameArr[0]);
	}

	/**
	 * It takes text and regular expression as input and returns all groups for all
	 * matched records. Retruns List<Map<Key,value>> where Key is groupNumber and
	 * value is matched text. List contains all matches.
	 */
	public static List<Map<Integer, String>> evalRegexAndReturn(String exp, String text) {
		List<Map<Integer, String>> allMatches = new ArrayList<>();

		final Pattern pattern = Pattern.compile(exp, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			Map<Integer, String> eachMatch = new HashMap<>();

			for (int groupNumber = 1; groupNumber <= matcher.groupCount(); groupNumber++) {
				eachMatch.put(groupNumber, matcher.group(groupNumber).trim());
			}

			allMatches.add(eachMatch);
		}

		return allMatches;
	}

	public static String yesterday(String dateformat) {
		return yesterday(dateformat, getDefaultTimezone());
	}

	public static String yesterday(String dateformat, String timezone) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(dateformat);
		DateTime dt = null;
		if (StringUtils.isNotBlank(timezone)) {
			dt = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezone))).minusDays(1);
		} else {
			dt = DateTime.now().minusDays(1);
		}
		String dateTime = dt.toString(dtf);
		return dateTime;
	}

	public static String addDaysToDate(String fromDateFormat, String fromDate, int noOfDaysToAdd,
			String outputDateformat) throws BishopRuleViolationException {
		Date date;
		try {
			date = date(fromDate, fromDateFormat, "");
		} catch (ParseException e) {
			throw new BishopRuleViolationException("Unable to get date for " + fromDate, e);
		}
		return addDaysToDate(date, noOfDaysToAdd, outputDateformat);

	}

	public static String addDaysToDate(Date fromDate, int noOfDaysToAdd, String outputDateformat) {
		return addDaysToDate(fromDate, noOfDaysToAdd, outputDateformat, getDefaultTimezone());
	}

	public static String addDaysToDate(Date fromDate, int noOfDaysToAdd, String outputDateformat, String timezone) {
		SimpleDateFormat dtf = new SimpleDateFormat(outputDateformat);

		if (StringUtils.isNotBlank(timezone)) {
			dtf.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		c.add(Calendar.DATE, noOfDaysToAdd);
		String output = dtf.format(c.getTime());
		return output;
	}

	/**
	 * This method takes input as dates and their dateFormat(should be same for both
	 * dates) and returns higher date
	 * 
	 * @param dateFormat
	 * @param dateStr1
	 * @param dateStr2
	 * @returns higher date
	 * @throws Exception
	 */
	public static String compareDate(String dateFormat, String dateStr1, String dateStr2) throws Exception {
		try {
			SimpleDateFormat fromSdf = new SimpleDateFormat(dateFormat);
			Date date1 = fromSdf.parse(dateStr1);
			Date date2 = fromSdf.parse(dateStr2);
			if (date1.compareTo(date2) > 0) {
				return fromSdf.format(date1);
			} else {
				return fromSdf.format(date2);
			}
		} catch (Exception e) {
			LOGGER.error("Error in comparing date " + dateStr1 + " and " + dateStr2, e);
			throw new Exception("Error in comparing date " + dateStr1 + " and " + dateStr2, e);
		}
	}

	/**
	 * @param exp  - regular expression
	 * @param text - text to dig in
	 * @param i    - group no
	 * @return first matched text
	 */
	public static String evalRegexAndReturnFirstMatch(String exp, String text, int i) {
		final Pattern pattern = Pattern.compile(exp, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		String result = "";
		if (matcher.find()) {
			result = matcher.group(i);
		}
		return result.trim();
	}

	/**
	 * @param exp  - regular expression
	 * @param text - text to dig in
	 * @param i    - group no
	 * @return all matched texts as List
	 */
	public static List<String> evalRegexAndReturnAllMatches(String exp, String text, int i) {
		List<String> allMatches = new ArrayList<>();
		final Pattern pattern = Pattern.compile(exp, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		String result = "";
		while (matcher.find()) {
			result = matcher.group(i);
			allMatches.add(result.trim());
		}
		return allMatches;
	}

	/**
	 * Find the difference in days between 2 dates. Convert date to yyyy-MM-dd
	 * 
	 * @param date1      - smaller date
	 * @param date2      - later date
	 * @param dateFormat - dateformat of the dates
	 * @return
	 */

	public static long dateDiff(String date1, String date2, String dateFormat) throws Exception {
		return dateDiff(ParseUtils.formatDate(dateFormat, ParseUtils.YYYY_DASH_MM_DASH_DD, date1),
				ParseUtils.formatDate(dateFormat, ParseUtils.YYYY_DASH_MM_DASH_DD, date2));
	}

	/**
	 * Find the difference in days between 2 dates. Should be date format yyyy-MM-dd
	 * 
	 * @param date1 - smaller date
	 * @param date2 - later date
	 * @return
	 */
	public static long dateDiff(String date1, String date2) {
		LocalDate dateBefore = LocalDate.parse(date1);
		LocalDate dateAfter = LocalDate.parse(date2);

		// calculating number of days in between
		long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		return noOfDaysBetween;
	}

	/**
	 * This method returns last date of previous month
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getLastDateofPreviousMonth(String format) throws Exception {
		try {
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.add(Calendar.MONTH, -1);
			aCalendar.set(Calendar.DAY_OF_MONTH, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

			SimpleDateFormat toSdf = new SimpleDateFormat(format);
			return toSdf.format(aCalendar.getTime());
		} catch (Exception e) {
			LOGGER.error("Error in getting last date of previous month", e);
			throw new Exception("Error in getting last date of previous month", e);
		}
	}

	/**
	 * This method returns last date of Provided month by format in current year
	 * 
	 * @param month
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static String getLastDateByMonth(String month, String format) throws Exception {
		try {
			Date date = new SimpleDateFormat("MMMM").parse(month);
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(date);
			aCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

			Date lastDateOfPreviousMonth = aCalendar.getTime();
			String dateLast = ParseUtils.formatDate(format, lastDateOfPreviousMonth);
			return dateLast;
		} catch (Exception e) {
			LOGGER.error("Error in getting last date of previous month", e);
			throw new Exception("Error in getting last date of previous month", e);
		}

	}

	/**
	 * Formats the input number in ###-##-#### format most suited for SSN e.g. input
	 * = 123456789 pattern = ###-##-#### returns 123-45-6789
	 * 
	 * 
	 * @param input
	 * @return
	 */
	public static String formatNumber(String input) {
		return formatNumber("###-##-####", input);
	}

	/**
	 * Format the input number in the provided pattern. e.g. input = 123456789
	 * pattern = ###-##-#### returns 123-45-6789
	 * 
	 * @param pattern
	 * @param input
	 * @return
	 */
	public static String formatNumber(String pattern, String input) {
		try {
			if (StringUtils.isBlank(input))
				return StringUtils.EMPTY;
			MaskFormatter maskFormatter = new MaskFormatter(pattern);
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(input);
		} catch (Exception e) {
			LOGGER.error("Unable to format text " + input, e);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Two decimal number is compared and returned integer. Results depend on the
	 * returned value as mentioned below. =0 : Two numbers are equal <0 : First
	 * number is less than second number >0 : First number is greater than second
	 * number
	 * 
	 * @param number1AsString
	 * @param number2AsString
	 * @return
	 * @throws BishopRuleViolationException
	 */
	public static int compareTwoNumbers(String number1AsString, String number2AsString) throws Exception {
		try {
			Double number1 = Double.parseDouble(keepDecimals(number1AsString));
			Double number2 = Double.parseDouble(keepDecimals(number2AsString));
			return Double.compare(number1, number2);
		} catch (NumberFormatException e) {
			throw new Exception(
					String.format("Error while comparing two numbers %s and %s", number1AsString, number2AsString), e);
		}
	}

	public static String addTwoNumbers(String number1AsString, String number2AsString) throws Exception {
		try {
			Double number1 = 0.00;
			Double number2 = 0.00;

			number1AsString = keepDecimals(number1AsString);
			if (StringUtils.isNotBlank(number1AsString)) {
				number1 = Double.parseDouble(number1AsString);
			}

			number2AsString = keepDecimals(number2AsString);
			if (StringUtils.isNotBlank(number2AsString)) {
				number2 = Double.parseDouble(number2AsString);
			}

			Double total = number1 + number2;
			total = (double) Math.round(total * 100d) / 100d;
			if (compareTwoNumbers("" + total, "0.00") == 0) {
				return StringUtils.EMPTY;
			} else {
				return "" + total;
			}
		} catch (NumberFormatException e) {
			throw new Exception(
					String.format("Error while adding two numbers %s and %s", number1AsString, number2AsString), e);
		}
	}

	public static Double subtractTwoNumbers(String number1AsString, String number2AsString) throws Exception {
		try {
			Double number1 = 0.00;
			Double number2 = 0.00;

			number1AsString = keepDecimals(number1AsString);
			if (StringUtils.isNotBlank(number1AsString)) {
				number1 = Double.parseDouble(number1AsString);
			}

			number2AsString = keepDecimals(number2AsString);
			if (StringUtils.isNotBlank(number2AsString)) {
				number2 = Double.parseDouble(number2AsString);
			}

			Double diff = number1 - number2;
			diff = (double) Math.round(diff * 100d) / 100d;
			return diff;
		} catch (NumberFormatException e) {
			throw new Exception(
					String.format("Error while subtracting two numbers %s and %s", number1AsString, number2AsString),
					e);
		}
	}

	/**
	 * Convert Microsoft un OLE Automation - OADate to Java Date.
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date convertFromOADate(double d) throws ParseException {
		return convertFromOADate(d, getDefaultTimezone());
	}

	public static Date convertFromOADate(double d, String timezone) throws ParseException {
		Calendar c = Calendar.getInstance();
		try {
			double mantissa = d - (long) d;
			double hour = mantissa * 24;
			double min = (hour - (long) hour) * 60;
			double sec = (min - (long) min) * 60;

			SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");

			if (StringUtils.isNotBlank(timezone)) {
				myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			}

			Date baseDate = myFormat.parse("30 12 1899");
			c.setTime(baseDate);
			c.add(Calendar.DATE, (int) d);
			c.add(Calendar.HOUR, (int) hour);
			c.add(Calendar.MINUTE, (int) min);
			c.add(Calendar.SECOND, (int) sec);
		} catch (Exception e) {
			LOGGER.error("Error in converting OADate to Date", e);
		}

		return c.getTime();
	}

	public static String getPreviousWeekDate(DayOfWeek day, String format) {
		ZonedDateTime input = ZonedDateTime.now();
		return input.minusWeeks(1).with(day).format(java.time.format.DateTimeFormatter.ofPattern(format));
	}

	public static String getPreviousOrSameWeekDate(DayOfWeek day) {
		return LocalDate.now().with(TemporalAdjusters.previousOrSame(day)).toString();
	}

	/**
	 * Add BatchID and BatchDate to rows. To be used for an ETL bot
	 * 
	 * @param rows
	 * @return
	 */
	public static List<Map> addBatchInfo(List<Map> rows) {
		if (G.executionMetrics.getFlow().getFeatureToggle().getCustomBoolean("sqlBatchId")) {
			List<Map> newrows = new ArrayList<Map>();
			for (Map m : rows) {
				Map m2 = new HashMap<>();
				m2.putAll(m);
				m2.put("BatchID", G.getBatchId());
				m2.put("BQBatchTimestamp", G.getBatchDate());
				newrows.add(m2);
			}
			return newrows;
		}
		return rows;
	}

	public static String getAbsoluteValue(String amountAsString) throws Exception {
		try {
			Double amount = 0.00;

			amountAsString = keepDecimals(amountAsString);
			if (StringUtils.isNotBlank(amountAsString)) {
				amount = Double.parseDouble(amountAsString);
				amount = Math.abs(amount);
			}

			if (compareTwoNumbers("" + amount, "0.00") == 0) {
				return StringUtils.EMPTY;
			} else {
				return "" + amount;
			}
		} catch (NumberFormatException e) {
			throw new Exception(String.format("Error while getting absolute value of amount %s", amountAsString), e);
		}
	}

	public static String removeSpacialChar(String text) {
		return ParseUtils.normHeaderForFoundry(text).replaceAll("[^a-zA-Z0-9\\s]", StringUtils.EMPTY);
	}

	public static String removeSelectedSpacialChar(String text, String spcialCharRegx) {
		return text.replaceAll(spcialCharRegx, StringUtils.EMPTY);
	}

	public static boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String generatePassword(String username, int length) throws Exception {
		String generatedpassword = StringUtils.EMPTY;
		try {
			if (length > 4) {

				length = length - 1; // first character would be Capital

				char[] oSymbols = "$@#_".toCharArray();
				char[] oLowerCase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				char[] oUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
				char[] oNumbers = "0123456789".toCharArray();
				char[] oAllChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
				Random orandom = new SecureRandom();

				char[] password = new char[length];

				// get the requirements out of the way
				char oFirstChar = oUpperCase[orandom.nextInt(oUpperCase.length)]; // fixed character
				password[0] = oLowerCase[orandom.nextInt(oLowerCase.length)];
				password[1] = oNumbers[orandom.nextInt(oNumbers.length)];
				password[2] = oSymbols[orandom.nextInt(oSymbols.length)];

				// populate rest of the password with random chars
				for (int i = 3; i < length; i++) {
					password[i] = oAllChars[orandom.nextInt(oAllChars.length)];
				}

				// shuffle it up
				for (int i = 0; i < password.length; i++) {
					int randomPosition = orandom.nextInt(password.length);
					char temp = password[i];
					password[i] = password[randomPosition];
					password[randomPosition] = temp;
				}

				generatedpassword = oFirstChar + new String(password);

				if (StringUtils.contains(generatedpassword, username)) {
					generatePassword(username, length);
				}
			}
		} catch (Exception e) {
			throw new BishopRuleViolationException("Password Generation failed.", e);
		}
		return generatedpassword;
	}

}
