package botrix.internal.gson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc.PgArray;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lombok.SneakyThrows;
import rpa.core.file.ParseUtils;
import rpa.core.utils.DateFormat;
import rpa.core.utils.DateFormat.DateFormatType;

public class TypeAdapters {
	public static final TypeAdapter<LocalDate> LOCAL_DATE;
	public static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME;
	public static final TypeAdapter<LocalTime> LOCAL_TIME;
	public static final TypeAdapter<PgArray> PG_ARRAY;
	public static final TypeAdapter<Long> LONG;
	public static final TypeAdapter<Integer> INTEGER;
	public static final TypeAdapter<Double> DOUBLE;
	public static final TypeAdapter<Float> FLOAT;
//	public static final TypeAdapter<String> STRING;
	private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;

	static {
		LOCAL_DATE = new LocalDateTypeAdapter();
		LOCAL_TIME = new LocalTimeTypeAdapter();
		LOCAL_DATE_TIME = new LocalDateTimeTypeAdapter();
		PG_ARRAY = new PgArrayTypeAdapter();

		LONG = new LongTypeAdapter();
		DOUBLE = new DoubleTypeAdapter();
		INTEGER = new IntegerTypeAdapter();
		FLOAT = new FloatTypeAdapter();
//		STRING = new StringTypeAdapter();

		WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);
		WRAPPER_TYPE_MAP.put(Integer.class, int.class);
		WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
		WRAPPER_TYPE_MAP.put(Character.class, char.class);
		WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
		WRAPPER_TYPE_MAP.put(Double.class, double.class);
		WRAPPER_TYPE_MAP.put(Float.class, float.class);
		WRAPPER_TYPE_MAP.put(Long.class, long.class);
		WRAPPER_TYPE_MAP.put(Short.class, short.class);
		WRAPPER_TYPE_MAP.put(Void.class, void.class);
	}

	static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
		@Override
		public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
			jsonWriter.value(localDate.toString());
		}

		@Override
		@SneakyThrows
		public LocalDate read(final JsonReader jsonReader) throws IOException {
			if (jsonReader.peek().equals(JsonToken.NULL)) {
				jsonReader.nextNull();
				return null;
			}

			String date = DateFormat.cleanDate(jsonReader.nextString());
			if (StringUtils.isEmpty(date))
				return null;
			try {
				return LocalDate.parse(date);
			} catch (Exception e) {
				try {
					return DateFormat.findFormat(date, DateFormatType.Date).localDate(date);
				} catch (Exception e1) {
					// using this for _BoardMeeting
					return DateFormat.findFormat(date, DateFormatType.DateTime).localDateTime(date).toLocalDate();
				}
			}
		}
	}

	static class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {
		@Override
		public void write(final JsonWriter jsonWriter, final LocalTime localDate) throws IOException {
			jsonWriter.value(localDate.toString());
		}

		@Override
		public LocalTime read(final JsonReader jsonReader) throws IOException {
			if (jsonReader.peek().equals(JsonToken.NULL)) {
				jsonReader.nextNull();
				return null;
			}

			return LocalTime.parse(jsonReader.nextString());
		}
	}

	static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
		@Override
		public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
			jsonWriter.value(localDate == null ? null : localDate.toString());
		}

		@Override
		@SneakyThrows
		public LocalDateTime read(final JsonReader jsonReader) throws IOException {
			if (jsonReader.peek().equals(JsonToken.NULL)) {
				jsonReader.nextNull();
				return null;
			}

			if (jsonReader.peek() == JsonToken.NUMBER) {
				long epochTimeMillis = jsonReader.nextLong(); // Example epoch time in milliseconds
				Instant instant = Instant.ofEpochMilli(epochTimeMillis);

				ZoneId zoneId = ZoneId.systemDefault(); // Use the system default time zone
				return instant.atZone(zoneId).toLocalDateTime();
			}

			String date = DateFormat.cleanDate(jsonReader.nextString());
			if (StringUtils.isEmpty(date))
				return null;
			try {
				return LocalDateTime.parse(date);
			} catch (Exception e) {
				return DateFormat.findFormat(date, DateFormatType.DateTime).localDateTime(date);
			}
		}
	}

	static class PgArrayTypeAdapter extends TypeAdapter<PgArray> {
		@Override
		@SneakyThrows
		public void write(JsonWriter out, PgArray value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}
			out.beginArray();
			for (Object obj : (Object[]) value.getArray()) {
				out.value(obj.toString());
			}
			out.endArray();
		}

		@Override
		public PgArray read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException();
		}
	}

	static class DoubleTypeAdapter extends TypeAdapter<Double> {
		@Override
		@SneakyThrows
		public void write(JsonWriter out, Double value) throws IOException {
			out.value(value);
		}

		@Override
		public Double read(JsonReader in) throws IOException {
			if (in.peek().equals(JsonToken.NULL)) {
				in.nextNull();
				return null;
			}

			String value = in.nextString();

			try {
				return StringUtils.isBlank(value) ? null : Double.valueOf(value);
			} catch (Exception e) {
				value = cleanNumber(value);
			}

			return StringUtils.isBlank(value) ? null : Double.valueOf(value);
		}
	}

	static class LongTypeAdapter extends TypeAdapter<Long> {
		@Override
		@SneakyThrows
		public void write(JsonWriter out, Long value) throws IOException {
			out.value(value);
		}

		@Override
		public Long read(JsonReader in) throws IOException {
			if (in.peek().equals(JsonToken.NULL)) {
				in.nextNull();
				return null;
			}

			String value = in.nextString();

			try {
				return StringUtils.isBlank(value) ? null : Long.valueOf(value);
			} catch (Exception e) {
				value = cleanNumber(value);
			}

			return StringUtils.isBlank(value) ? null : Long.valueOf(value);
		}
	}

	static class FloatTypeAdapter extends TypeAdapter<Float> {
		@Override
		@SneakyThrows
		public void write(JsonWriter out, Float value) throws IOException {
			out.value(value);
		}

		@Override
		public Float read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			String value = in.nextString();
			try {
				return StringUtils.isBlank(value) ? null : Float.valueOf(value);
			} catch (NumberFormatException e) {
				value = cleanNumber(value);
			}

			return StringUtils.isBlank(value) ? null : Float.valueOf(value);
		}
	}

//	static class StringTypeAdapter extends TypeAdapter<String> {
//		@Override
//		@SneakyThrows
//		public void write(JsonWriter out, String value) throws IOException {
//			out.value(value);
//		}
//
//		@Override
//		public String read(JsonReader in) throws IOException {
//			if (in.peek().equals(JsonToken.NULL)) {
//				in.nextNull();
//				return null;
//			}
//
//			if (in.peek().equals(JsonToken.BEGIN_ARRAY) || in.peek().equals(JsonToken.BEGIN_OBJECT)) {
//				throw new BishopRuntimeException("Can't convert array or object to string");
//			}
//
//			if (in.peek().equals(JsonToken.NUMBER)) {
//				return in.nextString();
//			} else if (in.peek().equals(JsonToken.BOOLEAN)) {
//				return Boolean.toString(in.nextBoolean());
//			}
//
//			return in.nextString();
//		}
//	}

	static class IntegerTypeAdapter extends TypeAdapter<Integer> {
		@Override
		@SneakyThrows
		public void write(JsonWriter out, Integer value) throws IOException {
			out.value(value);
		}

		@Override
		public Integer read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			String value = in.nextString();

			try {
				return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
			} catch (NumberFormatException e) {
				value = cleanNumber(value);
			}

			return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
		}
	}

	private static String cleanNumber(String number) {
		String trimmed = StringUtils.trimToEmpty(number);

		if (trimmed.equals("-"))
			return null;

		if (trimmed.equalsIgnoreCase("nil"))
			return "0";
		return ParseUtils.keepDecimals(number);
	}
}
