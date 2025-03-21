package rpa.commons.hibernate;

import java.lang.reflect.Field;

import javax.persistence.Id;

public class CompositeIdUtil {
	public static String getCompositeIdAsString(Object pojoInstance) {
		Class<?> pojoClass = pojoInstance.getClass();
		Field[] fields = pojoClass.getDeclaredFields();
		StringBuilder compositeIdBuilder = new StringBuilder();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				field.setAccessible(true);
				try {
					Object fieldValue = field.get(pojoInstance);
					compositeIdBuilder.append(fieldValue.toString()).append("-");
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		// Remove the trailing hyphen
		if (compositeIdBuilder.length() > 0) {
			compositeIdBuilder.setLength(compositeIdBuilder.length() - 1);
		}

		return compositeIdBuilder.toString();
	}
}
