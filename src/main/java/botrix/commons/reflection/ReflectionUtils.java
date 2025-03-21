package botrix.commons.reflection;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import rpa.core.file.FileHandlingUtils;

public class ReflectionUtils {
	private static final String CLASSPATH = File.separator + Paths.get("target", "classes").toString() + File.separator;

	// non instantiable
	private ReflectionUtils() {
	}

	public static Class<?> findClass(String className) throws ClassNotFoundException {
		className = className.replace(".java", "");

		Class<?> klass = null;
		for (File file : FileHandlingUtils.getListOfAllFiles(".", className + ".class")) {
			String path = file.getAbsolutePath();
			path = StringUtils.substringAfter(path, CLASSPATH);
			path = path.replace(".class", "").replace(File.separator, ".");
			klass = Class.forName(path);
		}
		return klass;
	}
}