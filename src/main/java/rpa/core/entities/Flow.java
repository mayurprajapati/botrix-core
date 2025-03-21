package rpa.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import lombok.Getter;
import lombok.Setter;
import rpa.core.driver.G;

@IgnoreExtraProperties
public class Flow {
	@Getter
	private List<_BotInfo> bots = new ArrayList<>();
	@Getter
	@Setter
	private String BishopAccount = StringUtils.EMPTY;
	@Getter
	@Setter
	private String flowId = StringUtils.EMPTY;
	@Getter
	@Setter
	private String flowName = StringUtils.EMPTY;
	@Getter
	@Setter
	private String flowDesc = StringUtils.EMPTY;
	@Setter
	private Object schedule = StringUtils.EMPTY;
	@Getter
	@Setter
	private String jenkinsURL = StringUtils.EMPTY;
	@Getter
	@Setter
	private String userId = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromProjectName = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromSystem = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toSystem = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromObject = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromCredential = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromLoginURL = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromEnvironment = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toEnvironment = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toProjectName = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toObject = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toCredential = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toLoginURL = StringUtils.EMPTY;
	@Getter
	@Setter
	private String fromCompany = StringUtils.EMPTY;
	@Getter
	@Setter
	private String toCompany = StringUtils.EMPTY;
	@Getter
	@Setter
	private String automationType = StringUtils.EMPTY;
	@Setter
	private String dbName = StringUtils.EMPTY;
	@Setter
	private String dbHostPort = StringUtils.EMPTY;
	@Getter
	@Setter
	private FeatureToggle featureToggle = null;
	@Getter
	@Setter
	private String BishopProjectId = StringUtils.EMPTY;
	@Setter
	private String BishopObject = StringUtils.EMPTY;
	@Getter
	@Setter
	private String recordReferenceDocId = StringUtils.EMPTY;
	@Getter
	@Setter
	private String from = StringUtils.EMPTY;
	@Getter
	@Setter
	private String to = StringUtils.EMPTY;
	@Getter
	@Setter
	private Invoice invoice = null;
	@Getter
	@Setter
	private Timestamp lastRun = null;
	@Getter
	@Setter
	private String flowGroup = StringUtils.EMPTY;
	@Getter
	@Setter
	private String workflowId = StringUtils.EMPTY;
	@Getter
	@Setter
	private String customAttributeMapping = StringUtils.EMPTY;
	@Getter
	@Setter
	private AutoDocs autoDocs = new AutoDocs();
	@Getter
	@Setter
	private Email emailConfig;
	@Getter
	@Setter
	private String mappingWorkbook;
//	@Getter @Setter private AutomateNotifications automateNotifications;
	@Getter
	@Setter
	private String developerName;
	@Getter
	@Setter
	private Map<String, String> bigQueries = new HashMap<>();
	@Getter
	private Map<Integer, List<Integer>> dependency = new HashMap<>();
	@Setter
	private String auditTrailEvaluatorId = "default";
	@Getter
	@Setter
	private String implementationStage = "live"; // live, development, deactivated
	@Getter
	@Setter
	private int manualTimePerTransaction = 0;

	/*
	 * These attributes are from Credential document
	 */
	@Getter
	@Setter
	private String url = StringUtils.EMPTY;
	@Getter
	@Setter
	private String username = StringUtils.EMPTY;
	@Getter
	@Setter
	private String password = StringUtils.EMPTY;
	@Getter
	@Setter
	private String environment = StringUtils.EMPTY;
	@Getter
	@Setter
	private String companyName = StringUtils.EMPTY;

	@Setter
	private String project = StringUtils.EMPTY;
	@Setter
	private List<String> email = new ArrayList<>();
	@Getter
	@Setter
	private Credentials credential;

	public Flow fetch() {
		return fetch(System.getProperty("username"), System.getProperty("password"), System.getProperty("url"),
				System.getProperty("project"), System.getProperty("environment"), System.getProperty("client"),
				System.getProperty("frequency"));
	}

	public Flow fetch(String username, String password, String url, String project, String environment, String client,
			String frequency) {
		Flow flow = new Flow();
		flow.setUsername(username);
		flow.setPassword(password);
		flow.setUrl(url);
		flow.setProject(project);
		flow.setEnvironment(environment);
		flow.setCompanyName(client);
		return flow;
	}

	public void setBots(List<_BotInfo> bots) {
		if (bots == null) {
			bots = new ArrayList<_BotInfo>();
		}
		for (int i = 0; i < bots.size(); i++) {
			_BotInfo bot = bots.get(i);
			if (bot.getIndex() == -1) {
				bot.setIndex(i);
			}
		}
		// sort them by _index from firebase, so that they can be run sequentially
		bots.sort((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
		this.bots = bots;
	}

	public String getAuditTrailEvaluatorId() {
		if (StringUtils.isBlank(auditTrailEvaluatorId))
			auditTrailEvaluatorId = "default";
		return auditTrailEvaluatorId;
	}

	public String getProject() {
		return StringUtils.trimToEmpty(project);
	}

	public String getSchedule() {
		return String.valueOf(schedule);
	}

	public List<String> getEmail() {
		if (email == null)
			return new ArrayList<String>();
		return email;
	}

	public String getBishopObject() {
		if (StringUtils.isBlank(BishopObject))
			return G.executionMetrics.getObject();
		return BishopObject;
	}

	public String getDbName() {
		if (StringUtils.isBlank(dbName))
			return fromCompany;
		else
			return dbName;
	}

	public String getDbHostPort() {
		if (StringUtils.isBlank(dbHostPort))
			return fromLoginURL;
		return dbHostPort;
	}

	public void setDependency(Map<String, List<Integer>> dependency) {
		for (Entry<String, List<Integer>> d : dependency.entrySet()) {
			this.dependency.put(Integer.parseInt(d.getKey()), d.getValue());
		}
	}

	@Exclude
	public String getSupportiveBotId(_BotInfo currentBot) {
		int index = currentBot.getIndex();
		for (Entry<Integer, List<Integer>> dep : getDependency().entrySet()) {
			if (dep.getValue().contains(index)) {
				_BotInfo dependsOn = getBots().stream().filter((bot) -> {
					return bot.getIndex() == dep.getKey();
				}).findFirst().get();
				return dependsOn.getId();
			}
		}
		return null;
	}

	public static _BotInfo findBotWithIndex(Flow flow, int botIndexFromFirebase) {
		for (_BotInfo bot : flow.getBots()) {
			if (bot.getIndex() >= 0 && bot.getIndex() == botIndexFromFirebase) {
				return bot;
			}
		}
		return flow.getBots().get(botIndexFromFirebase);
	}

	@Override
	public String toString() {
		return "Flow [bots=" + bots + ", BishopAccount=" + BishopAccount + ", flowId=" + flowId + ", flowName=" + flowName
				+ ", flowDesc=" + flowDesc + ", schedule=" + schedule + ", jenkinsURL=" + jenkinsURL + ", userId="
				+ userId + ", fromProjectName=" + fromProjectName + ", fromSystem=" + fromSystem + ", toSystem="
				+ toSystem + ", fromObject=" + fromObject + ", fromCredential=" + fromCredential + ", fromLoginURL="
				+ fromLoginURL + ", fromEnvironment=" + fromEnvironment + ", toEnvironment=" + toEnvironment
				+ ", toProjectName=" + toProjectName + ", toObject=" + toObject + ", toCredential=" + toCredential
				+ ", toLoginURL=" + toLoginURL + ", fromCompany=" + fromCompany + ", toCompany=" + toCompany
				+ ", automationType=" + automationType + ", dbName=" + dbName + ", dbHostPort=" + dbHostPort
				+ ", featureToggle=" + featureToggle + ", BishopProjectId=" + BishopProjectId + ", BishopObject=" + BishopObject
				+ ", recordReferenceDocId=" + recordReferenceDocId + ", from=" + from + ", to=" + to + ", invoice="
				+ invoice + ", lastRun=" + lastRun + ", flowGroup=" + flowGroup + ", workflowId=" + workflowId
				+ ", customAttributeMapping=" + customAttributeMapping + ", autoDocs=" + autoDocs + ", emailConfig="
				+ emailConfig + ", mappingWorkbook=" + mappingWorkbook + ", automateNotifications=" + ", developerName="
				+ developerName + ", bigQueries=" + bigQueries + ", url=" + url + ", username=" + username
				+ ", password=" + "********" + ", environment=" + environment + ", companyName=" + companyName
				+ ", project=" + project + ", email=" + email + ", credential=" + credential + ", dependency="
				+ dependency + "]";
	}

}
