package twostepverification;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.entities.Email;
import rpa.core.exceptions.BishopExitWithoutActionException;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.ParseUtils;

public class Email2SV {

	private static Logger LOGGER = LoggerFactory.getLogger(Email2SV.class);

	/**
	 * 2 Step Verification based on email. The emails should be sent to a br.iq
	 * email address. Contact Bishop IT Infra team to setup an email address which
	 * should be of the format BishopAccount-ipa@br.iq The email and its "app specific
	 * password" should be saved in firebase in collection "automation-core"
	 * Password saved should be encrypted
	 * 
	 * 
	 * To enable 2sv, update featureToggle > twoStepVerification attribute with
	 * required values The data is deserialized as @TwoStepVerification type - email
	 * username - email address from - email address of the sender phrase - a
	 * partial phrase from email subject to filter relevant emails
	 * 
	 * @return
	 * @throws BishopExitWithoutActionException
	 */
	public static String get() throws BishopRuleViolationException {
		Date date = new Date();
		date = DateUtils.addMinutes(date, (-1));
		if (G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification() == null || StringUtils
				.isBlank(G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getUsername())) {
			LOGGER.error("No 2SV config set. Please save 2SV config in flow");
			throw new BishopRuleViolationException("No 2SV config set. Please save 2SV config in flow");
		}
		String email = G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getUsername();
		String password = "";
		String fromEmail = G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getFrom();
		String subject = G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getPhrase();
		if (StringUtils.isBlank(subject))
			subject = "";
		return get(email, password, fromEmail, subject, date);

	}

	public static String getUsingCustom() throws BishopRuleViolationException {
		Date date = new Date();
		date = DateUtils.addMinutes(date, (-1));
		if (G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification() == null || StringUtils
				.isBlank(G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getUsername())) {
			throw new BishopRuleViolationException("No 2SV config found. Please save 2SV config in flow");
		}
		String email = G.executionMetrics.getFlow().getFeatureToggle().getCustomString("username");
		String password = "";
		String fromEmail = G.executionMetrics.getFlow().getFeatureToggle().getCustomString("from");
		String subject = G.executionMetrics.getFlow().getFeatureToggle().getCustomString("phrase");
		if (StringUtils.isBlank(subject))
			subject = "";
		return Email2SV.get(email, password, fromEmail, subject, date);
	}

	/**
	 * 2 Step Verification based on email. The emails should be sent to a br.iq
	 * email address. Contact Bishop IT Infra team to setup an email address which
	 * should be of the format BishopAccount-ipa@br.iq The email and its "app specific
	 * password" should be saved in firebase in collection "automation-core"
	 * Password saved should be encrypted
	 * 
	 * 
	 * To enable 2sv, update featureToggle > twoStepVerification attribute with
	 * required values The data is deserialized as @TwoStepVerification type - email
	 * username - email address from - email address of the sender phrase - a
	 * partial phrase from email subject to filter relevant emails
	 * 
	 * @return
	 * @throws BishopExitWithoutActionException
	 */
	public static String get(String email, String password, String fromEmail, String subject, Date date)
			throws BishopRuleViolationException {
		int retries = 1;
		String verificationCode = "";
		try {
			do {
				String text = Email.getLatestEmail(email, password, fromEmail, subject, date);
				verificationCode = getCode(ParseUtils.removeHtml(text));
				if (StringUtils.isBlank(verificationCode)) {
					LOGGER.info("Waiting for 15 secs to verification code");
					G.wait.sleep(15);
				} else {
					return verificationCode;
				}
				retries++;
			} while (retries <= 20 && StringUtils.isBlank(verificationCode));
		} catch (Exception e) {
			LOGGER.error(String.format("2SV code not retrieved from %s", email), e);
			throw new BishopRuleViolationException(String.format("2SV code not retrieved from %s", email), e);
		}
		if (StringUtils.isBlank(verificationCode)) {
			LOGGER.error(String.format("2SV code not retrieved from %s", email));
			throw new BishopRuleViolationException(String.format("2SV code not retrieved from %s", email));

		}
		return verificationCode;
	}

	/**
	 * Parses the verification code from text. Parses only 4-6 length of
	 * verification codes
	 * 
	 * @param text
	 * @return
	 */
	public static String getCode(String text) {
		LOGGER.info("Getting verification code");
		Pattern pattern = Pattern.compile("\\b\\d{4,7}\\b");
		Matcher matcher = pattern.matcher(ParseUtils.checkAndRemoveHTML(text));
		if (matcher.find()) {
			return StringUtils.trim(matcher.group(0));
		}
		return StringUtils.EMPTY;

	}
}
