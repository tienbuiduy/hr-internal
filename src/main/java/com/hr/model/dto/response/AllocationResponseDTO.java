package com.hr.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class AllocationResponseDTO {
	private int id;
	private int employeeId;
	private String employeeName;
	private int roleId; // project role
	private String roleName;
	private float allo;
	private String mainSkill;
	private int projectId;
	private String projectName;
	private int pmId;
	private String pmName;
	private int oppId;
	private String oppName;
	private int planPmId;
	private String planPmName;
	private Timestamp startDate;
	private Timestamp endDate;
	private String status;
	private float rate;
	private String note;
	private String source;
	private String department;
	private String type;

	public AllocationResponseDTO() {
	}

	public AllocationResponseDTO(int id, int employeeId, String employeeName, int roleId, String roleName, float allo,
			String mainSkill, int projectId, String projectName, int pmId, String pmName, int oppId, String oppName,
			int planPmId, String planPmName, Timestamp startDate, Timestamp endDate, String status, float rate, String note, String source, String department, String type) {
		this.id = id;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.roleId = roleId;
		this.roleName = roleName;
		this.allo = allo;
		this.mainSkill = mainSkill;
		this.projectId = projectId;
		this.projectName = projectName;
		this.pmId = pmId;
		this.pmName = pmName;
		this.oppId = oppId;
		this.oppName = oppName;
		this.planPmId = planPmId;
		this.planPmName = planPmName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.rate = rate;
		this.note = note;
		this.source = source;
		this.department = department;
		this.type = type;
	}

	@JsonProperty
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty
	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	@JsonProperty
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@JsonProperty
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@JsonProperty
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@JsonProperty
	public float getAllo() {
		return allo;
	}

	public void setAllo(float allo) {
		this.allo = allo;
	}

	@JsonProperty
	public String getMainSkill() {
		return mainSkill;
	}

	public void setMainSkill(String mainSkill) {
		this.mainSkill = mainSkill;
	}

	@JsonProperty
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	@JsonProperty
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@JsonProperty
	public int getOppId() {
		return oppId;
	}

	public void setOppId(int oppId) {
		this.oppId = oppId;
	}

	@JsonProperty
	public String getOppName() {
		return oppName;
	}

	public void setOppName(String oppName) {
		this.oppName = oppName;
	}

	@JsonProperty
	public int getPlanPmId() {
		return planPmId;
	}

	public void setPlanPmId(int planPmId) {
		this.planPmId = planPmId;
	}

	@JsonProperty
	public String getPlanPmName() {
		return planPmName;
	}

	public void setPlanPmName(String planPmName) {
		this.planPmName = planPmName;
	}

	@JsonProperty
	public int getPmId() {
		return pmId;
	}

	public void setPmId(int pmId) {
		this.pmId = pmId;
	}

	@JsonProperty
	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	@JsonProperty
	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	@JsonProperty
	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	@JsonProperty
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty
	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
