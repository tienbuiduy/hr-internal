package com.hr.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "project")
public class Project extends AbstractAuditEntity {
	private static final long serialVersionUID = 1L;

	private String projectName;
	private int contractTypeId;
	private String rank; // app_params.code
	private int customerId;
	private int pmId;
	private int qaId;
	private float headCountSize;
	private float mmSize;
	private Timestamp startDate;
	private Timestamp endDate;
	private Timestamp renewContract;
	private String overview;
	private String note;
	private String status; // status name
	private int oppId;
	private int isDeleted = 0;
	private String technicalSkill;

	@Basic
	@Column(name = "project_name")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Basic
	@Column(name = "contract_type_id")
	public int getContractTypeId() {
		return contractTypeId;
	}

	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}

	@Basic
	@Column(name = "rank")
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@Basic
	@Column(name = "customer_id")
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	@Basic
	@Column(name = "pm_id")
	public int getPmId() {
		return pmId;
	}

	public void setPmId(int pmId) {
		this.pmId = pmId;
	}

	@Basic
	@Column(name = "qa_id")
	public int getQaId() {
		return qaId;
	}

	public void setQaId(int qaId) {
		this.qaId = qaId;
	}

	@Basic
	@Column(name = "head_count_size")
	public float getHeadCountSize() {
		return headCountSize;
	}

	public void setHeadCountSize(float headCountSize) {
		this.headCountSize = headCountSize;
	}

	@Basic
	@Column(name = "mm_size")
	public float getMmSize() {
		return mmSize;
	}

	public void setMmSize(float mmSize) {
		this.mmSize = mmSize;
	}

	@Basic
	@Column(name = "start_date")
	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	@Basic
	@Column(name = "end_date")
	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	@Basic
	@Column(name = "renew_contract")
	public Timestamp getRenewContract() {
		return renewContract;
	}

	public void setRenewContract(Timestamp renewContract) {
		this.renewContract = renewContract;
	}

	@Basic
	@Column(name = "overview")
	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	@Basic
	@Column(name = "note")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Basic
	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Basic
	@Column(name = "opp_id")
	public int getOppId() {
		return oppId;
	}

	public void setOppId(int oppId) {
		this.oppId = oppId;
	}

	@Basic
	@Column(name = "is_deleted")
	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Basic
	@Column(name = "technical_skill")
	public String getTechnicalSkill() {
		return technicalSkill;
	}

	public void setTechnicalSkill(String technicalSkill) {
		this.technicalSkill = technicalSkill;
	}
}
