package botrix.internal.utils;

import java.util.Optional;

public class TextUtils {
	// (is|otp|password|key|code|CODE|KEY|OTP|PASSWORD)\s*-?\s*([0-9]{4,8})

	private TextUtils() {
	}

	public static Optional<String> getOtp(String text, int digits) {
		return Optional.ofNullable(RegexUtils.firstMatch(text, "(\\d{" + digits + "})", 1));
	}

	public static Optional<String> getOtp(String text) {
		return Optional.ofNullable(RegexUtils.firstMatch(text, "(\\d{4,7})", 1));
	}

}
