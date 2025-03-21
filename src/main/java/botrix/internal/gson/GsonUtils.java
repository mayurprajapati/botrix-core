package botrix.internal.gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.postgresql.jdbc.PgArray;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
	static final ObjectMapper mapper = new ObjectMapper()//
			.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

	static {
		setupGson();
	}

	public static Gson gson;

	private static void setupGson() {
		gson = new GsonBuilder()//
				.registerTypeAdapter(LocalDate.class, TypeAdapters.LOCAL_DATE) //
				.registerTypeAdapter(LocalTime.class, TypeAdapters.LOCAL_TIME) //
				.registerTypeAdapter(LocalDateTime.class, TypeAdapters.LOCAL_DATE_TIME) //
				.registerTypeAdapter(PgArray.class, TypeAdapters.PG_ARRAY) //
				.registerTypeAdapterFactory(com.google.gson.internal.bind.TypeAdapters.newFactory(long.class,
						Long.class, TypeAdapters.LONG)) //
				.registerTypeAdapterFactory(com.google.gson.internal.bind.TypeAdapters.newFactory(int.class,
						Integer.class, TypeAdapters.INTEGER)) //
				.registerTypeAdapterFactory(com.google.gson.internal.bind.TypeAdapters.newFactory(float.class,
						Float.class, TypeAdapters.FLOAT)) //
				.registerTypeAdapterFactory(com.google.gson.internal.bind.TypeAdapters.newFactory(double.class,
						Double.class, TypeAdapters.DOUBLE)) //
//				.registerTypeAdapterFactory(com.google.gson.internal.bind.TypeAdapters.newFactory(String.class,
//						String.class, TypeAdapters.STRING)) //
				.create();
	}

	public static <T> T fromObjectToPojo(Object obj, Class<T> klass) {
		return mapper.convertValue(obj, klass);
//		JsonElement json = gson.toJsonTree(obj);
//		return gson.fromJson(json, klass);
	}
}
