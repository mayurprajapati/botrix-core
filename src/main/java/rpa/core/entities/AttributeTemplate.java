package rpa.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import rpa.core.driver.G;
import rpa.core.metrics.Helper;

public class AttributeTemplate {

	private String BishopAccount;
	private String BishopUniqueId;
	private String currentSystem;
	private String object;
	private String companyName;
	private String timeExtracted;
	private String project;
	private String linkedUuid;
	private String flowId;
	private String objectNumber = StringUtils.EMPTY;
	
	@Getter @Setter private boolean isLocked = false;

	private String attachmentDirectory = StringUtils.EMPTY;
	private String autoDocsCardId = StringUtils.EMPTY;
	private String rawText = StringUtils.EMPTY;
	private String autoDocsDocumentId = StringUtils.EMPTY;
	private String autoDocsFileName = StringUtils.EMPTY;
	private String autoDetectedVendor = StringUtils.EMPTY;
	private String recordDocumentId = StringUtils.EMPTY;
	private List<JobCostCode> jobCostCodes = new ArrayList<>();
	private List<OverheadCostCode> overheadCostCodes = new ArrayList<>();

	public static class JobCostCode {
		private String project = StringUtils.EMPTY;
		private String phase = StringUtils.EMPTY;
		private String costCode = StringUtils.EMPTY;
		private String costType = StringUtils.EMPTY;
		private String amount = StringUtils.EMPTY;

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getPhase() {
			return phase;
		}

		public void setPhase(String phase) {
			this.phase = phase;
		}

		public String getCostCode() {
			return costCode;
		}

		public void setCostCode(String costCode) {
			this.costCode = costCode;
		}

		public String getCostType() {
			return costType;
		}

		public void setCostType(String costType) {
			this.costType = costType;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}
	}

	public static class OverheadCostCode {
		private String company = StringUtils.EMPTY;
		private String department = StringUtils.EMPTY;
		private String glAccount = StringUtils.EMPTY;
		private String amount = StringUtils.EMPTY;

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getGlAccount() {
			return glAccount;
		}

		public void setGlAccount(String glAccount) {
			this.glAccount = glAccount;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}
	}

	public List<JobCostCode> getJobCostCodes() {
		return jobCostCodes;
	}

	public void setJobCostCodes(List<JobCostCode> jobCostCodes) {
		this.jobCostCodes = jobCostCodes;
	}

	public List<OverheadCostCode> getOverheadCostCodes() {
		return overheadCostCodes;
	}

	public void setOverheadCostCodes(List<OverheadCostCode> overheadCostCodes) {
		this.overheadCostCodes = overheadCostCodes;
	}

	public String getCurrentSystem() {
		return G.executionMetrics.getCurrentSystem();
	}

	public String getObject() {
		return G.executionMetrics.getObject();
	}

	public String getAutoDocsDocumentId() {
		return autoDocsDocumentId;
	}

	public void setAutoDocsDocumentId(String autoDocsDocumentId) {
		this.autoDocsDocumentId = autoDocsDocumentId;
	}

	public String getCompanyName() {
		return G.executionMetrics.getBotInstall().getCompanyName();
	}

	public String getTimeExtracted() {
		return Helper.getTimeNow();
	}

	public String getProject() {
		return G.executionMetrics.getBotInstall().getProject();
	}

	public String getLinkedUuid() {
		return G.executionMetrics.getMasterUuid();
	}

	public String getBishopAccount() {
		return G.executionMetrics.getBishopAccount();
	}

	public String getFlowId() {
		return G.executionMetrics.getBotInstall().getFlowId();
	}

	public void setBishopAccount(String BishopAccount) {
		this.BishopAccount = BishopAccount;
	}

	public void setCurrentSystem(String currentSystem) {
		this.currentSystem = currentSystem;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setTimeExtracted(String timeExtracted) {
		this.timeExtracted = timeExtracted;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public void setLinkedUuid(String linkedUuid) {
		this.linkedUuid = linkedUuid;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getObjectNumber() {
		return G.executionMetrics.getObjectNumber();
	}

	public void setObjectNumber(String objectNumber) {
		this.objectNumber = objectNumber;
	}

	public String getAttachmentDirectory() {
		return attachmentDirectory;
	}

	public void setAttachmentDirectory(String attachmentDirectory) {
		this.attachmentDirectory = attachmentDirectory;
	}

	public String getBishopUniqueId() {
		if (StringUtils.isNotBlank(G.executionMetrics.getBishopUniqueId()))
			return G.executionMetrics.getBishopUniqueId();
		return BishopUniqueId;
	}

	public void setBishopUniqueId(String BishopId) {
		if (StringUtils.isBlank(G.executionMetrics.getBishopUniqueId()))
			this.BishopUniqueId = BishopId;
		else
			this.BishopUniqueId = G.executionMetrics.getBishopUniqueId();
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}

	public String getAutoDetectedVendor() {
		return autoDetectedVendor;
	}

	public void setAutoDetectedVendor(String autoDetectedVendor) {
		this.autoDetectedVendor = autoDetectedVendor;
	}

	public String getRecordDocumentId() {
		return recordDocumentId;
	}

	public void setRecordDocumentId(String recordDocumentId) {
		this.recordDocumentId = recordDocumentId;
	}

	public String getAutoDocsCardId() {
		return autoDocsCardId;
	}

	public void setAutoDocsCardId(String autoDocsCardId) {
		this.autoDocsCardId = autoDocsCardId;
	}

	public String getAutoDocsFileName() {
		return autoDocsFileName;
	}

	public void setAutoDocsFileName(String autoDocsFileName) {
		this.autoDocsFileName = autoDocsFileName;
	}

}
