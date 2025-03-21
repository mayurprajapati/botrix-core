package rpa.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Invoice {

	private List<String> amountFields = new ArrayList<>();
	private List<String> mandatoryFields = new ArrayList<>();
	private Map<String, String> dateFields = new HashMap<>();
	private Map<String, String> total = new HashMap<>();
	private boolean multiPageInvoice = false;
	private List<String> uniqueKeys = new ArrayList<>();
	private List<Map<String, String>> rules = new ArrayList<>();
	private boolean enterSingleLineItemOnly = false;
	private boolean enterNoLineItems = false;
	private List<String> anchorKeys = new ArrayList<>();

	public boolean isEnterNoLineItems() {
		return enterNoLineItems;
	}

	public void setEnterNoLineItems(boolean enterNoLineItems) {
		this.enterNoLineItems = enterNoLineItems;
	}

	public boolean isEnterSingleLineItemOnly() {
		return enterSingleLineItemOnly;
	}

	public void setEnterSingleLineItemOnly(boolean enterSingleLineItemOnly) {
		this.enterSingleLineItemOnly = enterSingleLineItemOnly;
	}

	public List<String> getAmountFields() {
		return amountFields;
	}

	public void setAmountFields(List<String> amountFields) {
		this.amountFields = amountFields;
	}

	public Map<String, String> getDateFields() {
		return dateFields;
	}

	public void setDateFields(Map<String, String> dateFields) {
		this.dateFields = dateFields;
	}

	public List<String> getMandatoryFields() {
		return mandatoryFields;
	}

	public void setMandatoryFields(List<String> mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}

	public Map<String, String> getTotal() {
		return total;
	}

	public void setTotal(Map<String, String> total) {
		this.total = total;
	}

	public boolean isMultiPageInvoice() {
		return multiPageInvoice;
	}

	public void setMultiPageInvoice(boolean multiPageInvoice) {
		this.multiPageInvoice = multiPageInvoice;
	}

	public List<String> getUniqueKeys() {
		return uniqueKeys;
	}

	public void setUniqueKeys(List<String> uniqueKeys) {
		this.uniqueKeys = uniqueKeys;
	}

	public List<Map<String, String>> getRules() {
		return rules;
	}

	public void setRules(List<Map<String, String>> rules) {
		this.rules = rules;
	}

	public List<String> getAnchorKeys() {
		return anchorKeys;
	}

	public void setAnchorKeys(List<String> anchorKeys) {
		this.anchorKeys = anchorKeys;
	}
}
