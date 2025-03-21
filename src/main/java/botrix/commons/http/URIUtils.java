package botrix.commons.http;

import java.net.URI;

import lombok.SneakyThrows;

public class URIUtils {

	@SneakyThrows
	public static URI getUri(String url) {
		return new URI(url);
	}
}
