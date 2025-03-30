package botrix.utils;

import java.nio.charset.Charset;
import java.nio.file.Path;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class FileUtils {
	private static final AppDirs appDirs = AppDirsFactory.getInstance();

	public static String getCacheDir() {
		return appDirs.getUserCacheDir("autoengine", "1.0", "mayur");
	}

	public static void write(Path path, String content) {
		try {
			org.apache.commons.io.FileUtils.write(path.toFile(), content, Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String readString(Path path) {
		try {
			return java.nio.file.Files.readString(path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
