package rpa.core.reflection;

import org.apache.commons.lang3.ArrayUtils;

public class Helper {
	private Helper() {
	}

	public static boolean isInstanceOf(Class<?> klass, String simpleName) {
		if (klass == null)
			return false;
		if (klass.getSimpleName().equalsIgnoreCase(simpleName))
			return true;

		// check if instance of any of the interfaces
		if (ArrayUtils.isNotEmpty(klass.getInterfaces())) {
			for (Class<?> i : klass.getInterfaces()) {
				if (isInstanceOf(i, simpleName))
					return true;
			}
		}

		return isInstanceOf(klass.getSuperclass(), simpleName);
	}
}
