package rpa.core.metrics;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import botrix.internal.logging.LoggerFactory;
import lombok.Getter;
import lombok.Setter;
import rpa.core.entities.Company;
import rpa.core.entities.Constants;
import rpa.core.entities.Flow;
import rpa.core.entities._BotInfo;
import rpa.core.metrics.ExecutionMetrics.JenkinsBuildInfo.Params;

public class ExecutionMetrics {

	@Getter @Setter private String masterUuid;
	@Getter @Setter private String executionStartTime;
	@Getter @Setter private String executionEndTime;
	@Getter @Setter private String frequency;
	@Getter @Setter private String bot;
	@Getter @Setter private String currentSystem;
	@Getter @Setter private String object;
	@Getter @Setter private String objectNumber;
	@Getter @Setter private String alternateObjectNumber;
	@Getter @Setter private String BishopUniqueId;
	@Getter @Setter private String objectUrl;
	@Getter @Setter private String attachments;
	@Getter @Setter private Flow flow;
	@Setter private String BishopAccount;
	@Getter @Setter private String flowId;
	@Setter private boolean vpn;
	@Getter @Setter private String previousSystem;
	@Getter @Setter private String previousSystemObjectId;
	@Getter @Setter private String autoDocsDocumentId;
	@Getter @Setter private String autoDocsFileName;
	@Getter @Setter private Map<String, List<Map<String, String>>> mappingSheet = new HashMap<>();
	@Getter @Setter private int recordsProcessed;
	@Getter @Setter private Company company;
	@Getter @Setter private Map<String, List<Map<String, String>>> bigQueryData = new HashMap<>();
	@Getter @Setter private Map<String, List<Map<String, String>>> databaseQueryData = new HashMap<>();
	@Getter @Setter private JenkinsBuildInfo jenkinsBuildInfo = new JenkinsBuildInfo();

	private static Logger LOGGER = LoggerFactory.getLogger(ExecutionMetrics.class);

	public ExecutionMetrics(String flowId, String BishopAccount, String bot, String object, String currentSystem,
			_BotInfo _botInfo) throws Exception {
		this.setJenkinsBuildInfo(generateJenkinsBuildInfo());
		LOGGER.info("Setting up execution metrics");
		masterUuid = Thread.currentThread().getName();
		executionStartTime = Thread.currentThread().getName();
		executionEndTime = StringUtils.EMPTY;
		boolean fromOrTo = false;
		if (bot.equals(Constants.EXTRACT)) {
			fromOrTo = true;
		}
		this.bot = bot;
		this.object = object;
		this.currentSystem = currentSystem;
		this.frequency = "";
		this.objectNumber = "";
		this.attachments = "";
		this.flowId = flowId;
	}

	private JenkinsBuildInfo generateJenkinsBuildInfo() {
		JenkinsBuildInfo info;
		String buildInfoBase64Encoded = System.getProperty("buildInfo");
		if (isNotBlank(buildInfoBase64Encoded)) {
			String buildInfoJson = new String(Base64.getDecoder().decode(buildInfoBase64Encoded));
			LOGGER.info("JENKINS BUILD INFORMATION\n" + buildInfoJson);
			info = new GsonBuilder().create().fromJson(buildInfoJson, JenkinsBuildInfo.class);
		} else {
			info = new JenkinsBuildInfo();
			Params params = new Params();
			params.setFlowId(flowId);
			params.setBishopAccount(BishopAccount);
			params.setAdditionalParam(System.getProperty("additionalParam", ""));
			info.setParams(params);
		}
		return info;
	}

	public Flow getBotInstall() {
		return getFlow();
	}

	public String getBishopAccount() {
		if (StringUtils.isBlank(BishopAccount)) {
			BishopAccount = System.getProperty("BishopAccount");
		}
		return BishopAccount;
	}

	public boolean isVpn() {
		return vpn;
	}

	public static class JenkinsBuildInfo {
		@Getter @Setter private Params params = new Params();
		@SerializedName(value = "JOB_DISPLAY_URL") @Getter @Setter 
		private String jobDisplayUrl = EMPTY;
		@SerializedName(value = "RUN_DISPLAY_URL")@Getter @Setter 
		private String runDisplayUrl = EMPTY;
		@SerializedName(value = "BUILD_NUMBER")@Getter @Setter 
		private String buildNumber = EMPTY;
		@SerializedName(value = "BUILD_ID")@Getter @Setter 
		private String buildId = EMPTY;
		@SerializedName(value = "NODE_NAME")@Getter @Setter 
		private String nodeName = EMPTY;
		@SerializedName(value = "NODE_LABELS")@Getter @Setter 
		private String nodeLabels = EMPTY;
		@SerializedName(value = "WORKSPACE")@Getter @Setter 
		private String workspace = EMPTY;
		@SerializedName(value = "WORKSPACE_TMP")@Getter @Setter 
		private String workspaceTmp = EMPTY;
		@SerializedName(value = "JENKINS_URL")@Getter @Setter 
		private String jenkinsUrl = EMPTY;
		@SerializedName(value = "BUILD_URL")@Getter @Setter 
		private String buildUrl = EMPTY;
		@SerializedName(value = "JOB_URL")@Getter @Setter 
		private String jobUrl = EMPTY;
		@Getter @Setter private String userId = EMPTY;
		@Getter @Setter private String userName = EMPTY;

		public static class Params {
			@Getter @Setter private String flowId = EMPTY;
			@Getter @Setter private String BishopAccount = EMPTY;
			@SerializedName(value = "NUMBER")
			@Getter @Setter private String additionalParam = EMPTY;
		}
	}
}
