package botrix.internal.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.compress.utils.IOUtils;

import com.itextpdf.io.util.ResourceUtil;

import lombok.SneakyThrows;

public class ResourceUtils {

	private ResourceUtils() {
	}

	/**
	 * Gets the resource's inputstream.
	 *
	 * @param key the full name of the resource.
	 * @return the {@code InputStream} to get the resource or {@code null} if not
	 *         found.
	 */
	public static InputStream getResourceStream(String key) {
		return getResourceStream(key, null);
	}

	@SneakyThrows
	public static String readString(String key) {
		InputStream stream = getResourceStream(key);
		return org.apache.commons.io.IOUtils.toString(stream, StandardCharsets.UTF_8);
	}

	/**
	 * Gets the resource's inputstream.
	 *
	 * @param key    the full name of the resource.
	 * @param loader the ClassLoader to load the resource or null to try the ones
	 *               available.
	 * @return the {@code InputStream} to get the resource or {@code null} if not
	 *         found.
	 */
	public static InputStream getResourceStream(String key, ClassLoader loader) {
		if (key.startsWith("/")) {
			key = key.substring(1);
		}
		InputStream stream = null;
		if (loader != null) {
			stream = loader.getResourceAsStream(key);
			if (stream != null) {
				return stream;
			}
		}
		// Try to use Context Class Loader to load the properties file.
		try {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null) {
				stream = contextClassLoader.getResourceAsStream(key);
			}
		} catch (SecurityException ignored) {
		}

		if (stream == null) {
			stream = ResourceUtil.class.getResourceAsStream("/" + key);
		}
		if (stream == null) {
			stream = ClassLoader.getSystemResourceAsStream(key);
		}
		return stream;
	}
}
