package rpa.core.metrics;

import java.util.Calendar;
import java.util.UUID;

import com.fasterxml.uuid.Generators;

import rpa.core.file.ParseUtils;

public class Helper {

	public static String createUUID() {
		UUID uuid1 = Generators.timeBasedGenerator().generate();
		return uuid1.toString();
	}

	public static String getTimeNow() {
		return ParseUtils.now("yyyy-MM-dd HH:mm:ss.SSSSSS", "UTC");
	}

	public static String getTimeNowInMillis() {
		return String.valueOf(Calendar.getInstance().getTimeInMillis());

	}
}
