package botrix.internal.prefs;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

import com.google.gson.Gson;

import botrix.internal.gson.GsonUtils;
import lombok.SneakyThrows;

public class Preferences {
	private java.util.prefs.Preferences prefs;
	private Gson gson = GsonUtils.gson;

	private Preferences(java.util.prefs.Preferences prefs, String uniqueName) {
		this.prefs = prefs;
	}

	public static Preferences user(String uniqueName) {
		return new Preferences(java.util.prefs.Preferences.userRoot(), uniqueName);
	}

	public static Preferences system(String uniqueName) {
		return new Preferences(java.util.prefs.Preferences.systemRoot(), uniqueName);
	}

	public void put(String key, Object obj) {
		String json = gson.toJson(obj);
		put(key, json);
	}

	public <T extends Serializable> void put(String key, T value) {
		prefs.putByteArray(key, SerializationUtils.serialize(value));
		sync();
	}

	private void sync() {
		try {
			prefs.sync();
		} catch (Exception e) {
		}
	}

	public String getString(String key, String defaultValue) {
		return get(key, String.class, defaultValue);
	}

	public Double getDouble(String key, Double defaultValue) {
		return get(key, Double.class, defaultValue);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		return get(key, Integer.class, defaultValue);
	}

	public Long getLong(String key, Long defaultValue) {
		return get(key, Long.class, defaultValue);
	}

	public Float getFloat(String key, Float defaultValue) {
		return get(key, Float.class, defaultValue);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		return get(key, Boolean.class, defaultValue);
	}

	public <T extends Serializable> T get(String key, Class<T> klass, T defaultValue) {
		byte[] val = prefs.getByteArray(key, SerializationUtils.serialize(defaultValue));
		return klass.cast(SerializationUtils.deserialize(val));
	}

	public <T extends Object> T get(String key, java.lang.reflect.Type type, T defaultValue) {
		String json = get(key, String.class, null);

		if (json == null)
			return defaultValue;

		return gson.fromJson(json, type);
	}

	@SneakyThrows
	public void clear() {
		prefs.clear();
	}
}
