package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class AllocationRequestDTO {
	private int id;
	private int employeeId;
	private int roleId;
	private float allo;
	private Integer projectId;
	private Integer oppId;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp startDate;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp endDate;
	private float rate;
	private String note;
	private String source;
	private String type;// type name

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
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@JsonProperty
	public float getAllo() {
		return allo;
	}

	public void setAllo(float allo) {
		this.allo = allo;
	}

	@JsonProperty
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	@JsonProperty
	public Integer getOppId() {
		return oppId;
	}

	public void setOppId(Integer oppId) {
		this.oppId = oppId;
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

	@JsonProperty
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
