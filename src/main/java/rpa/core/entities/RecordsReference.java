package rpa.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.Timestamp;

import rpa.core.driver.G;

public class RecordsReference {

	private String BishopAccount = G.executionMetrics.getBishopAccount();
	private String BishopProjectId = G.executionMetrics.getFlow().getBishopProjectId();
	private Timestamp createdAt = Timestamp.now();
	private Timestamp updatedAt = Timestamp.now();
	private String updatedByMasterUuid = G.executionMetrics.getMasterUuid();
	private String BishopObjectNumber;
	private String BishopObject = G.executionMetrics.getFlow().getBishopObject();
	private List<String> systems;
	private List<String> flowIds;
	private Map<String, String> ids;

	public static RecordsReference create(String recordNumber, String system, String flowId) {
		RecordsReference ref = new RecordsReference();
		List<String> systems = new ArrayList<String>();
		List<String> flowIds = new ArrayList<String>();
		Map<String, String> ids = new HashMap<String, String>();
		systems.add(system);
		flowIds.add(flowId);
		ids.put(system, recordNumber);
		ref.setBishopObjectNumber(recordNumber);
		ref.setSystems(systems);
		ref.setFlowIds(flowIds);
		ref.setIds(ids);

		return ref;
	}

	public static RecordsReference update(RecordsReference recRef, String recordNumber, String system, String flowId) {
		RecordsReference ref = recRef;
		List<String> systems = new ArrayList<String>(recRef.getSystems());
		List<String> flowIds = new ArrayList<String>(recRef.getFlowIds());
		Map<String, String> id = new HashMap<String, String>(recRef.getIds());
		systems.add(system);
		flowIds.add(flowId);
		id.put(system, recordNumber);
		ref.setSystems(systems);
		ref.setFlowIds(flowIds);
		ref.setIds(id);
		ref.setCreatedAt(recRef.getCreatedAt());
		return ref;
	}

	public static RecordsReference get(RecordsReference recRef, String recordNumber, String system, String flowId) {
//  TODO
		RecordsReference ref = recRef;
		List<String> systems = new ArrayList<String>(recRef.getSystems());
		List<String> flowIds = new ArrayList<String>(recRef.getFlowIds());
		Map<String, String> id = new HashMap<String, String>(recRef.getIds());
		systems.add(system);
		flowIds.add(flowId);
		id.put(system, recordNumber);
		ref.setSystems(systems);
		ref.setFlowIds(flowIds);
		ref.setIds(id);

		return ref;
	}

	public String getBishopAccount() {
		return G.executionMetrics.getBishopAccount();
	}

	public void setBishopAccount(String BishopAccount) {
		this.BishopAccount = BishopAccount;
	}

	public String getBishopObjectNumber() {
		return BishopObjectNumber;
	}

	public void setBishopObjectNumber(String BishopObjectNumber) {
		this.BishopObjectNumber = BishopObjectNumber;
	}

	public String getBishopProjectId() {
		return G.executionMetrics.getFlow().getBishopProjectId();
	}

	public void setBishopProjectId(String BishopProjectId) {
		this.BishopProjectId = BishopProjectId;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<String> getSystems() {
		return systems;
	}

	public void setSystems(List<String> systems) {
		this.systems = systems;
	}

	public List<String> getFlowIds() {
		return flowIds;
	}

	public void setFlowIds(List<String> flowIds) {
		this.flowIds = flowIds;
	}

	public Map<String, String> getIds() {
		return ids;
	}

	public void setIds(Map<String, String> ids) {
		this.ids = ids;
	}

	public String getUpdatedByMasterUuid() {
		return updatedByMasterUuid;
	}

	public void setUpdatedByMasterUuid(String updatedByMasterUuid) {
		this.updatedByMasterUuid = updatedByMasterUuid;
	}

	public String getBishopObject() {
		return BishopObject;
	}

	public void setBishopObject(String BishopObject) {
		this.BishopObject = BishopObject;
	}

}
