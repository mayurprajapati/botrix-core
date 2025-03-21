package rpa.core.audittrail.evaluator;

//import api.Slack;
//import rpa.core.file.AuditTrail;

/**
 * A callback interface with defined methods which can be called by
 * {@link AuditTrail} one by one to update data. <br>
 * 
 * Refer {@link AuditTrail#evaluateException} to check which callback will be
 * called when. <br>
 * 
 * Implement this interface at client level / flow level to intercept
 * AuditTrail/Email/Slack data.<br>
 * 
 * @author Mayur
 */
public interface AuditTrailEvaluator {
	/**
	 * Callback to update status <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateStatus(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update message <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateMessage(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update exception <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateException(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update error trace <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateErrorTrace(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update exception chain <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateExceptionChain(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update exception owner <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateExceptionOwner(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update exception classification <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateExceptionClassification(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update notification emails <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateNotificationEmails(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to update slack channels <br>
	 * Refer {@link AuditTrail#evaluateException} to see default values
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} filled with default values
	 */
	public default void updateNotificationSlackChannels(AuditTrailEvaluationResult result) {
	}

	/**
	 * Callback to notify {@link AuditTrailEvaluationResult#getSlackChannels()}
	 * about the {@link AuditTrailEvaluationResult#getMessage()}.
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} containing
	 *               {@link AuditTrailEvaluationResult#getSlackChannels()}
	 * @return true if successfully notified at least one channel else false
	 */
	public default boolean notifySlack(AuditTrailEvaluationResult result) {
		boolean isMsgSent = false;

		for (String channel : result.getSlackChannels()) {
//			Slack.sendMessage(channel, result.getMessage());
			isMsgSent = true;
		}

		return isMsgSent;
	}

	/**
	 * Callback to notify {@link AuditTrailEvaluationResult#getEmails()} about the
	 * message. <br>
	 * 
	 * Note: It this returns true then default notification system (works on GCP
	 * Functions) will be skipped else executed.<br>
	 * 
	 * @param result - {@link AuditTrailEvaluationResult} containing
	 *               {@link AuditTrailEvaluationResult#getEmails()}
	 * @return true if successfully notified at least one email else false
	 * @see AuditTrail#addToAuditTrail(String, String, Throwable)
	 */
	public default boolean notifyEmail(AuditTrailEvaluationResult result) {
		return false;
	}
}
