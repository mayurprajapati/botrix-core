package botrix.internal.utils.google;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import botrix.utils.ResourceUtils;

public class GoogleUtils {
	private GoogleUtils() {
	}

//	public static GoogleCredentials getCredentials() {
//		try {
//			return GoogleCredentials.fromStream(ResourceUtils.getResourceStream("credentials.json"));
//		} catch (IOException e) {
//			throw new BishopRuntimeException("Failed to create google credentials", e);
//		}
//	}

	public static GoogleCredential getCredentials(Collection<String> scopes) throws IOException {
		GoogleCredential credentials = GoogleCredential.fromStream(ResourceUtils.getResourceStream("credentials.json"));
		if (CollectionUtils.isNotEmpty(scopes))
			credentials = credentials.createScoped(scopes);
		return credentials;
	}
}
