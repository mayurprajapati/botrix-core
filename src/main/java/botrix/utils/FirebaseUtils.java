package botrix.utils;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import rpa.core.exceptions.BishopRuntimeException;
import rpa.core.file.FileHandlingUtils;

public class FirebaseUtils {
	private static Firestore firestore;

	private FirebaseUtils() {
	}

	public static Firestore getFirestore() {
		try {
			if (firestore != null)
				return firestore;

			FileInputStream in = new FileInputStream(System.getenv("GOOGLE_SERVICE_ACCOUNT"));
			GoogleCredentials credentials = GoogleCredentials.fromStream(in);
			FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);

			firestore = FirestoreClient.getFirestore();

			return firestore;
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to initialize Firestore", e);
		}
	}

}
