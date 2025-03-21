package rpa.commons.collection;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MapUtils {
	@SuppressWarnings("unchecked")
	public static String getString(@SuppressWarnings("rawtypes") Map map, String... keys) {
		for (String key : keys) {
			String finalKey = key;

			key = map.keySet().stream().filter((otherKey) -> otherKey.toString().trim().equals(finalKey)).findFirst()
					.orElse(key).toString();

			String value = StringUtils.trimToEmpty(org.apache.commons.collections4.MapUtils.getString(map, key));
			if (StringUtils.isNotBlank(value))
				return value;
		}

		return StringUtils.EMPTY;
	}
}
