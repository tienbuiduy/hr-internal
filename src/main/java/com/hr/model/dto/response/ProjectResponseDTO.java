package com.hr.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class ProjectResponseDTO {
	private int id;
	private String projectName;
	private int contractTypeId;
	private String rank;
	private String rankName;
	private int customerId;
	private int pmId;
	private int qaId;
	private float headCountSize;
	private float mmSize;
	private Timestamp startDate;
	private Timestamp endDate;
	private Timestamp renewContract;
	private String status; // status name
	private String technicalSkill;
	private String overview;
	private String note;
	private int oppId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@JsonProperty
	public int getContractTypeId() {
		return contractTypeId;
	}

	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}

	@JsonProperty
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@JsonProperty
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	@JsonProperty
	public int getPmId() {
		return pmId;
	}

	public void setPmId(int pmId) {
		this.pmId = pmId;
	}

	@JsonProperty
	public int getQaId() {
		return qaId;
	}

	public void setQaId(int qaId) {
		this.qaId = qaId;
	}

	@JsonProperty
	public float getHeadCountSize() {
		return headCountSize;
	}

	public void setHeadCountSize(float headCountSize) {
		this.headCountSize = headCountSize;
	}

	@JsonProperty
	public float getMmSize() {
		return mmSize;
	}

	public void setMmSize(float mmSize) {
		this.mmSize = mmSize;
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
	public Timestamp getRenewContract() {
		return renewContract;
	}

	public void setRenewContract(Timestamp renewContract) {
		this.renewContract = renewContract;
	}

	@JsonProperty
	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	@JsonProperty
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@JsonProperty
	public int getOppId() {
		return oppId;
	}

	public void setOppId(int oppId) {
		this.oppId = oppId;
	}

	@JsonProperty
	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty
	public String getTechnicalSkill() {
		return technicalSkill;
	}

	public void setTechnicalSkill(String technicalSkill) {
		this.technicalSkill = technicalSkill;
	}
}
