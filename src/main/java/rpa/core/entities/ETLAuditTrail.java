package rpa.core.entities;

public class ETLAuditTrail {

	private String message;
	private String company_id;
	private String workflow_id;
	private String workflow_type;
	private String execution_id;
	private String publisher_id;
	private String resource_id;
	private String status;
	private String flowGroupid;
	private String component_id;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getWorkflow_id() {
		return workflow_id;
	}

	public void setWorkflow_id(String workflow_id) {
		this.workflow_id = workflow_id;
	}

	public String getWorkflow_type() {
		return workflow_type;
	}

	public void setWorkflow_type(String workflow_type) {
		this.workflow_type = workflow_type;
	}

	public String getExecution_id() {
		return execution_id;
	}

	public void setExecution_id(String execution_id) {
		this.execution_id = execution_id;
	}

	public String getPublisher_id() {
		return publisher_id;
	}

	public void setPublisher_id(String publisher_id) {
		this.publisher_id = publisher_id;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFlowGroupid() {
		return flowGroupid;
	}

	public void setFlowGroupid(String flowGroupid) {
		this.flowGroupid = flowGroupid;
	}

	public String getComponent_id() {
		return component_id;
	}

	public void setComponent_id(String component_id) {
		this.component_id = component_id;
	}

}
