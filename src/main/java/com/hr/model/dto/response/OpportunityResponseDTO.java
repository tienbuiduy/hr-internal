package com.hr.model.dto.response;

import java.sql.Timestamp;

public class OpportunityResponseDTO {
	private int id;
	private String oppCode;
	private String oppName;
	private int contractType;
	private float mm;
	private float totalMm;
	private Timestamp startDate;
	private Timestamp endDate;
	private float duration;
	private float successRate;
	private String note;
	private int tempPm;
	private int planPm;
	private String overview;
	private int status;
	private String statusString;

	public OpportunityResponseDTO() {
	}

	public OpportunityResponseDTO(int id, String oppCode, String oppName, int contractType, float mm, float totalMm,
								  Timestamp startDate, Timestamp endDate, float duration, float successRate, String note,
								  int tempPm, int planPm, String overview, int status, String statusString) {
		this.id = id;
		this.oppCode = oppCode;
		this.oppName = oppName;
		this.contractType = contractType;
		this.mm = mm;
		this.totalMm = totalMm;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
		this.successRate = successRate;
		this.note = note;
		this.tempPm = tempPm;
		this.planPm = planPm;
		this.overview = overview;
		this.status = status;
		this.statusString = statusString;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOppCode() {
		return oppCode;
	}

	public void setOppCode(String oppCode) {
		this.oppCode = oppCode;
	}

	public String getOppName() {
		return oppName;
	}

	public void setOppName(String oppName) {
		this.oppName = oppName;
	}

	public int getContractType() {
		return contractType;
	}

	public void setContractType(int contractType) {
		this.contractType = contractType;
	}

	public float getMm() {
		return mm;
	}

	public void setMm(float mm) {
		this.mm = mm;
	}

	public float getTotalMm() {
		return totalMm;
	}

	public void setTotalMm(float totalMm) {
		this.totalMm = totalMm;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(float successRate) {
		this.successRate = successRate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}
}
