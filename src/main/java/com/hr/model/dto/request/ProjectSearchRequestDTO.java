package com.hr.model.dto.request;

public class ProjectSearchRequestDTO {
	private String projectName;
	private int contractTypeId;
	private String rankId;
	private int customerId;
	private int pmId;
	private int oppId;
	private String status; // status name
	private String technicalSkill;

	public ProjectSearchRequestDTO() {
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getContractTypeId() {
		return contractTypeId;
	}
	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}
	public String getRankId() {
		return rankId;
	}
	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getPmId() {
		return pmId;
	}
	public void setPmId(int pmId) {
		this.pmId = pmId;
	}
	public int getOppId() {
		return oppId;
	}
	public void setOppId(int oppId) {
		this.oppId = oppId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTechnicalSkill() {
		return technicalSkill;
	}

	public void setTechnicalSkill(String technicalSkill) {
		this.technicalSkill = technicalSkill;
	}
}
