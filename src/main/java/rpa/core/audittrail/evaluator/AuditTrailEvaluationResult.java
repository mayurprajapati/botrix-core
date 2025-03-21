package rpa.core.audittrail.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class AuditTrailEvaluationResult {
	private ExceptionOwner exceptionOwner;
	private String exceptionChain;
	private String errorTrace;
	private ExceptionClassification exceptionClassification;
	private String status;
	private String message;
	private Throwable exception;
	private List<String> emails = new ArrayList<>();
	private List<String> slackChannels = new ArrayList<>();
	private String templateId = StringUtils.EMPTY;
}
