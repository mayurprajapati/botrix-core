package botrix.internal.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils extends org.apache.tika.utils.RegexUtils {

	private RegexUtils() {
	}

	public static String firstMatch(String text, String regex) {
		return firstMatch(text, pattern(regex));
	}

	public static String firstMatch(String text, String regex, int group) {
		return firstMatch(text, pattern(regex), group);
	}

	public static String firstMatch(String text, Pattern pattern) {
		return firstMatch(text, pattern, -1);
	}

	public static String firstMatch(String text, Pattern pattern, int group) {
		try {
			Matcher m = pattern.matcher(text);
			m.find();
			return group == -1 ? m.group() : m.group(group);
		} catch (IllegalStateException ise) {
			return null;
		}
	}

	public static Pattern pattern(String regex) {
		return Pattern.compile(regex);
	}

	public static Pattern pattern(String regex, int flags) {
		return Pattern.compile(regex, flags);
	}

	public static List<String> allMatch(String text, String regex) {
		return allMatch(text, pattern(regex));
	}

	public static List<String> allMatch(String text, String regex, int group) {
		return allMatch(text, pattern(regex), group);
	}

	public static List<String> allMatch(String text, Pattern pattern) {
		return allMatch(text, pattern, -1);
	}

	public static List<String> allMatch(String text, Pattern pattern, int group) {
		Matcher matcher = pattern.matcher(text);

		List<String> list = new LinkedList<>();
		while (matcher.find()) {
			list.add(group == -1 ? matcher.group() : matcher.group(group));
		}

		return list;
	}

	public static void main(String[] args) {
		System.out.println(firstMatch("as", "a"));
	}
}
