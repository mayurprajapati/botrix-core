package rpa.core.audittrail.evaluator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionClassification {
	private String classification;

	public static class Defaults {
		public static final ExceptionClassification WORKFLOW_ERROR = new ExceptionClassification("Workflow Error");
		public static final ExceptionClassification WORKFLOW_ERROR_UNKNOWN = new ExceptionClassification(
				"Workflow Error - Unknown");
		public static final ExceptionClassification WORKFLOW_ERROR_UNEXPECTED_ALERT = new ExceptionClassification(
				"Workflow Error - Unexpected Alert");
		public static final ExceptionClassification WORKFLOW_ERROR_TIMEOUT = new ExceptionClassification(
				"Workflow Error - Timeout");
		public static final ExceptionClassification WORKFLOW_ERROR_VPN = new ExceptionClassification(
				"Workflow Error - VPN");
		public static final ExceptionClassification DATA_ERROR = new ExceptionClassification("Data Error");
		public static final ExceptionClassification LOGIN_ERROR = new ExceptionClassification("Login Error");

		private Defaults() {
		}
	}
}
