package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class OpportunitySearchRequestDTO implements Serializable {
	private static final long serialVersionUID = 8590264588068517259L;

	private String oppName;
	private int contractTypeId;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp fromStartDate;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp toStartDate;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp fromEndDate;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp toEndDate;
	private int tempPm;
	private int planPm;
	private List<String> status;

	public String getOppName() {
		return oppName;
	}

	public void setOppName(String oppName) {
		this.oppName = oppName;
	}

	public int getContractTypeId() {
		return contractTypeId;
	}

	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}

	public Timestamp getFromStartDate() {
		return fromStartDate;
	}

	public void setFromStartDate(Timestamp fromStartDate) {
		this.fromStartDate = fromStartDate;
	}

	public Timestamp getToStartDate() {
		return toStartDate;
	}

	public void setToStartDate(Timestamp toStartDate) {
		this.toStartDate = toStartDate;
	}

	public Timestamp getFromEndDate() {
		return fromEndDate;
	}

	public void setFromEndDate(Timestamp fromEndDate) {
		this.fromEndDate = fromEndDate;
	}

	public Timestamp getToEndDate() {
		return toEndDate;
	}

	public void setToEndDate(Timestamp toEndDate) {
		this.toEndDate = toEndDate;
	}

	public int getTempPm() {
		return tempPm;
	}

	public void setTempPm(int tempPm) {
		this.tempPm = tempPm;
	}

	public int getPlanPm() {
		return planPm;
	}

	public void setPlanPm(int planPm) {
		this.planPm = planPm;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}
}
