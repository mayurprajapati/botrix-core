package rpa.core.utils;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import botrix.internal.logging.LoggerFactory;
import botrix.utils.FirebaseUtils;
import botrix.utils.TextUtils;
import botrix.utils.WaitUtils;
import lombok.SneakyThrows;

public class FirebaseHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseHelper.class);

	@SneakyThrows
	public static Optional<String> getOtp(Duration timeout, int digit, Runnable action) {
		Timestamp timestamp = FirebaseUtils.getFirestore().collection("sms").limit(1)
				.orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING).get().get().getDocuments()
				.get(0).getTimestamp("timestamp");

		LOGGER.info("Timestamp.now() -> {}", timestamp);
		Query q = FirebaseUtils.getFirestore().collection("sms").whereGreaterThan("timestamp", timestamp);

		action.run();

		long seconds = timeout.getSeconds() < 10 ? 10 : timeout.getSeconds();
		long iterations = seconds / 10;

		for (long i = 0; i < iterations; i++) {
			for (QueryDocumentSnapshot snap : q.get().get().getDocuments()) {
				Map<String, Object> data = snap.getData();
				LOGGER.info("Potential OTP data: {}", data);

				String text = Objects.toString(data.get("displayMessage"));
				Optional<String> otp = TextUtils.getOtp(text, digit);
				if (otp.isPresent())
					return otp;
			}
			WaitUtils.sleepSeconds(10);
		}

		return Optional.empty();
	}
}
