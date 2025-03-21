package rpa.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@IgnoreExtraProperties

public class FeatureToggle {

	private boolean retryFailedRecords;
	private boolean recordBotVideo;
	private boolean procoreSubmittalFilterCurrentRev;
	private boolean procoreRfiQuestionAttachmentNames;
	private String statusTrigger;
	private String uploadDirectory;
	private String assigneeTrigger;
	private boolean mergeAttachments;
	private boolean disableNotifications;
	private boolean skipAttachments = true;
	private Map custom;
	private Map<String, String> status = new HashMap<>();
	private boolean exportRecord;
	private SlackCredential slack;
	private API api;
	@Getter @Setter private boolean enableParallelRun = false;

	public API getApi() {
		return api;
	}

	public void setApi(API api) {
		this.api = api;
	}

	public String getUploadDirectory() {
		return uploadDirectory;
	}

	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	private boolean recordReference;
	private TwoStepVerification twoStepVerification;

	public boolean isRetryFailedRecords() {
		return retryFailedRecords;
	}

	public void setRetryFailedRecords(boolean retryFailedRecords) {
		this.retryFailedRecords = retryFailedRecords;
	}

	public boolean isProcoreSubmittalFilterCurrentRev() {
		return procoreSubmittalFilterCurrentRev;
	}

	public void setProcoreSubmittalFilterCurrentRev(boolean procoreSubmittalFilterCurrentRev) {
		this.procoreSubmittalFilterCurrentRev = procoreSubmittalFilterCurrentRev;
	}

	public boolean isProcoreRfiQuestionAttachmentNames() {
		return procoreRfiQuestionAttachmentNames;
	}

	public void setProcoreRfiQuestionAttachmentNames(boolean procoreRfiQuestionAttachmentNames) {
		this.procoreRfiQuestionAttachmentNames = procoreRfiQuestionAttachmentNames;
	}

	public String getAssigneeTrigger() {
		return assigneeTrigger;
	}

	public void setAssigneeTrigger(String assigneeTrigger) {
		this.assigneeTrigger = assigneeTrigger;
	}

	public boolean isMergeAttachments() {
		return mergeAttachments;
	}

	public void setMergeAttachments(boolean mergeAttachments) {
		this.mergeAttachments = mergeAttachments;
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	public boolean isExportRecord() {
		return exportRecord;
	}

	public void setExportRecord(boolean exportRecord) {
		this.exportRecord = exportRecord;
	}

	public Object getCustom() {
		return custom;
	}

	public Map<String, String> getCustomMap(String field) {
		if (custom == null)
			return new HashMap<>();
//		if (custom != null && (custom.getClass().equals(Map.class) || custom.getClass().equals(HashMap.class))) {
		Object o = ((Map<String, Object>) custom).get(field);
		if (o != null && (o.getClass().equals(Map.class) || o.getClass().equals(HashMap.class))) {
			return (Map<String, String>) o;
		}
//		}
		return new HashMap<>();
	}

	public ArrayList<String> getCustomStringArray(String field) {
		if (custom == null)
			return new ArrayList<String>();
//    if (custom != null && (custom.getClass().equals(Map.class) || custom.getClass().equals(HashMap.class))) {
		Object o = ((Map<String, Object>) custom).get(field);
		if (o != null && (o.getClass().equals(ArrayList.class))) {
			return (ArrayList<String>) o;
		}
//    }
		return new ArrayList<String>();
	}

	public String getCustomString(String field) {
		if (custom == null)
			return null;
		Object o = ((Map<String, Object>) custom).get(field);
		if (custom != null && o != null && (o.getClass().equals(String.class) || o.getClass().equals(Integer.class))) {
			return (String) o;
		}
		return null;
	}

	/**
	 * Return @Boolean > true or false or null will return null if the attribute
	 * does not exists in firebase
	 * 
	 * @param field
	 * @return
	 */
	public Boolean getCustomBoolean(String field) {
		if (custom == null)
			return false;
		Object o = ((Map<String, Object>) custom).get(field);
		if (o != null && o.getClass().equals(Boolean.class)) {
			return Boolean.valueOf(String.valueOf(o));
		}
		return false;
	}

	/**
	 * Return @boolean > true or false. will return false if the attribute does not
	 * exists in firebase
	 * 
	 * @param field
	 * @return
	 */
	public Boolean getCustomBooleanDef(String field) {
		if (custom == null)
			return false;
		Object o = ((Map<String, Object>) custom).get(field);
		if (o != null && o.getClass().equals(Boolean.class)) {
			return Boolean.valueOf(String.valueOf(o));
		}
		return null;
	}

	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}

	public boolean isDisableNotifications() {
		return disableNotifications;
	}

	public void setDisableNotifications(boolean disableNotifications) {
		this.disableNotifications = disableNotifications;
	}

	public String getStatusTrigger() {
		return statusTrigger;
	}

	public void setStatusTrigger(String statusTrigger) {
		this.statusTrigger = statusTrigger;
	}

	@Data
	@AllArgsConstructor
	public static class SlackCredential {
		/**
		 * Required by firebase deserialization
		 */
		public SlackCredential() {
		}

		public SlackCredential(SlackCredential slack) {
			this.setBearer(slack.bearer);
			this.setChannelId(slack.channelId);
			this.setChannelName(slack.channelName);
		}

		private String bearer;
		private String channelId;
		private String channelName = StringUtils.EMPTY;
	}

	public static class API {
		private String host = StringUtils.EMPTY;
		private String apiVersion = StringUtils.EMPTY;
		private String grantType = StringUtils.EMPTY;
		private String companyId = StringUtils.EMPTY;
		private String companyName = StringUtils.EMPTY;
		private String projectName = StringUtils.EMPTY;
		private String projectId = StringUtils.EMPTY;
		private String customFilterLabel = StringUtils.EMPTY;
		private String customFilterValue = StringUtils.EMPTY;
		private String status = StringUtils.EMPTY;
		private String uploadPath = StringUtils.EMPTY;
		private String apiUrl = StringUtils.EMPTY;

		public String getApiVersion() {
			return apiVersion;
		}

		public void setApiVersion(String apiVersion) {
			this.apiVersion = apiVersion;
		}

		public String getGrantType() {
			return grantType;
		}

		public void setGrantType(String grantType) {
			this.grantType = grantType;
		}

		public String getApiUrl() {
			return apiUrl;
		}

		public void setApiUrl(String apiUrl) {
			this.apiUrl = apiUrl;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		private String scope = StringUtils.EMPTY;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

		public String getCustomFilterLabel() {
			return customFilterLabel;
		}

		public void setCustomFilterLabel(String customFilterLabel) {
			this.customFilterLabel = customFilterLabel;
		}

		public String getCustomFilterValue() {
			return customFilterValue;
		}

		public void setCustomFilterValue(String customFilterValue) {
			this.customFilterValue = customFilterValue;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUploadPath() {
			return uploadPath;
		}

		public void setUploadPath(String uploadPath) {
			this.uploadPath = uploadPath;
		}
	}

	public SlackCredential getSlack() {
		return slack;
	}

	public void setSlack(SlackCredential slack) {
		this.slack = slack;
	}

	public TwoStepVerification getTwoStepVerification() {
		return twoStepVerification;
	}

	public void setTwoStepVerification(TwoStepVerification twoStepVerification) {
		this.twoStepVerification = twoStepVerification;
	}

	public boolean isSkipAttachments() {
		return skipAttachments;
	}

	public void setSkipAttachments(boolean skipAttachments) {
		this.skipAttachments = skipAttachments;
	}

	
	public boolean isRecordBotVideo() {
		return recordBotVideo;
	}

	public void setRecordBotVideo(boolean recordBotVideo) {
		this.recordBotVideo = recordBotVideo;
	}
}
