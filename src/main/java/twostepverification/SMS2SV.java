package twostepverification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import com.google.cloud.firestore.Query.Direction;

import botrix.internal.logging.LoggerFactory;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import rpa.core.driver.G;
import rpa.core.exceptions.BishopExitWithoutActionException;
import rpa.core.exceptions.BishopRuleViolationException;

public class SMS2SV {

	private static Logger LOGGER = LoggerFactory.getLogger(SMS2SV.class);

	/**
	 * 2 Step Verification based on SMS. The sms should be sent to a twilio phone
	 * number
	 * #https://Bishop.atlassian.net/wiki/spaces/EN/pages/683737322/2+Step+Verification
	 * 
	 * Contact Bishop IT Infra team to setup a twilio phone number
	 * 
	 * To enable 2sv, update featureToggle > twoStepVerification attribute with
	 * required values The data is deserialized as @TwoStepVerification type - sms
	 * username - from - phone_number phrase - ,gets the latest 2fa code .
	 * 
	 * @return code
	 * @throws BishopExitWithoutActionException
	 */
	public static String get() throws BishopRuleViolationException {
		Date date = new Date();
		date = DateUtils.addMinutes(date, (-7));
		if (G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification() == null || StringUtils
				.isBlank(G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getUsername())) {
			LOGGER.error("No 2SV config set. Please save 2SV config in flow");
			throw new BishopRuleViolationException("No 2SV config set. Please save 2SV config in flow");
		}
		String to = G.executionMetrics.getFlow().getFeatureToggle().getTwoStepVerification().getUsername();
		return get(to);

	}

	/**
	 * 2 Step Verification based on SMS. The sms should be sent to a twilio phone
	 * number
	 * #https://Bishop.atlassian.net/wiki/spaces/EN/pages/683737322/2+Step+Verification
	 * 
	 * Contact Bishop IT Infra team to setup a twilio phone number
	 * 
	 * To enable 2sv, update featureToggle > twoStepVerification attribute with
	 * required values The data is deserialized as @TwoStepVerification type - sms
	 * username - to - phone_number phrase - ,gets the latest 2fa code for the to
	 * number.
	 * 
	 * @return code
	 * @throws BishopExitWithoutActionException
	 */

	public static String get(String to) throws BishopRuleViolationException {
//		int retries = 1;
//		List<String> verificationCodes = new ArrayList<String>();
//		do {
//			try {
//				List<QueryDocumentSnapshot> docs = FirestoreDB.db.collection("automate_twostep_auth_codes").document(to)
//						.collection("codes").orderBy("timestamp", Direction.DESCENDING).limit(5).get().get()
//						.getDocuments();
//				for (QueryDocumentSnapshot doc : docs) {
//					verificationCodes.add(doc.getString("code"));
//				}
//				G.wait.sleep(15);
//			} catch (Exception e) {
//				LOGGER.error(String.format("2SV code not retrieved from %s", to), e);
//				throw new BishopRuleViolationException(String.format("2SV code not retrieved from %s", to), e);
//			}
//		} while (retries <= 5 && CollectionUtils.isEmpty(verificationCodes));
//		if (CollectionUtils.isEmpty(verificationCodes)) {
//			LOGGER.error(String.format("2SV code not retrieved from %s", to));
//			throw new BishopRuleViolationException(String.format("2SV code not retrieved from %s", to));
//
//		}
//		return verificationCodes.get(0);
		return "";
	}
}
