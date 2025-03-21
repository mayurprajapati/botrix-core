package rpa.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AutoDocs {

	private String workflowId = StringUtils.EMPTY;
	private String cardId = StringUtils.EMPTY;
	private List<String> cardIds = new ArrayList<>();
	private String credential = StringUtils.EMPTY;
	private String lookBackDays = StringUtils.EMPTY;
	private boolean batchDocument = true;
	private boolean vendorDetectionRequired = true;
	private boolean enterSingleLineItemOnly = false;
	private boolean enterNoLineItems = false;

	public List<String> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<String> cardIds) {
		this.cardIds = cardIds;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public boolean isBatchDocument() {
		return batchDocument;
	}

	public void setBatchDocument(boolean batchDocument) {
		this.batchDocument = batchDocument;
	}

	public boolean isVendorDetectionRequired() {
		return vendorDetectionRequired;
	}

	public void setVendorDetectionRequired(boolean vendorDetectionRequired) {
		this.vendorDetectionRequired = vendorDetectionRequired;
	}

	public boolean isEnterSingleLineItemOnly() {
		return enterSingleLineItemOnly;
	}

	public void setEnterSingleLineItemOnly(boolean enterSingleLineItemOnly) {
		this.enterSingleLineItemOnly = enterSingleLineItemOnly;
	}

	public boolean isEnterNoLineItems() {
		return enterNoLineItems;
	}

	public void setEnterNoLineItems(boolean enterNoLineItems) {
		this.enterNoLineItems = enterNoLineItems;
	}

	public String getLookBackDays() {
		return lookBackDays;
	}

	public void setLookBackDays(String lookBackDays) {
		this.lookBackDays = lookBackDays;
	}
}
