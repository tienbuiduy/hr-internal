package com.hr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "opportunity")
public class Opportunity extends AbstractAuditEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "opp_code")
	private String oppCode;

	@Column(name = "opp_name")
	private String oppName;

	@Column(name = "contract_type_id")
	private int contractTypeId;

	@Column(name = "mm")
	private float mm;

	@Column(name = "total_mm")
	private float totalMm;

	@Column(name = "start_date")
	private Timestamp startDate;

	@Column(name = "end_date")
	private Timestamp endDate;

	@Column(name = "duration")
	private float duration;

	@Column(name = "success_rate")
	private float successRate;

	@Column(name = "note")
	private String note;

	@Column(name = "temp_pm_id")
	private int tempPmId;
	
	@Column(name = "plan_pm_id")
	private int planPmId;

	@Column(name = "overview")
	private String overview;

	@Column(name = "status")
	private String status; // status code

	@Column(name = "is_deleted")
	private int isDeleted = 0;

	public String getOppCode() {
		return oppCode;
	}

	public Opportunity() {
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

	public int getContractTypeId() {
		return contractTypeId;
	}

	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
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

	public int getTempPmId() {
		return tempPmId;
	}

	public void setTempPmId(int tempPmId) {
		this.tempPmId = tempPmId;
	}

	public int getPlanPmId() {
		return planPmId;
	}

	public void setPlanPmId(int planPmId) {
		this.planPmId = planPmId;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
}
