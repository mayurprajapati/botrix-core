package botrix.commons.twofactorauthentication;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import rpa.core.exceptions.BishopRuntimeException;

public class TwoFactorAuthUtils {
	private static final int GOOGLE_AUTH_TOTP_GENERATION_MAX_RETRIES = 10;

	public static String getGoogleAuthenticatorCode(String totpKey, int length) {
		// though the length is getting computed by google auth, sometimes it calculates
		// it wrong
		GoogleAuthenticator gAuth = new GoogleAuthenticator();

		for (int i = 0; i < GOOGLE_AUTH_TOTP_GENERATION_MAX_RETRIES; i++) {
			var totpcode = String.valueOf(gAuth.getTotpPassword(totpKey));
			if (totpcode.length() == length) {
				return totpcode;
			}
		}

		throw new BishopRuntimeException("Google Authenticator is constantly generating wrong codes. Tried %s times"
				.formatted(GOOGLE_AUTH_TOTP_GENERATION_MAX_RETRIES));
	}
}
