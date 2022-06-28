package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class ProjectRequestDTO {
    private int id;
    private String projectName;
    private int contractTypeId;
    private String rank;
    private int customerId;
    private int pmId;
    private int qaId;
    private float headCountSize;
    private float mmSize;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp endDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp renewContract;
    private String status; // status name
    private String overview;
    private String note;
    private int oppId;
    private String technicalSkill;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
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

    public int getQaId() {
        return qaId;
    }

    public void setQaId(int qaId) {
        this.qaId = qaId;
    }

    public float getHeadCountSize() {
        return headCountSize;
    }

    public void setHeadCountSize(float headCountSize) {
        this.headCountSize = headCountSize;
    }

    public float getMmSize() {
        return mmSize;
    }

    public void setMmSize(float mmSize) {
        this.mmSize = mmSize;
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

    public Timestamp getRenewContract() {
        return renewContract;
    }

    public void setRenewContract(Timestamp renewContract) {
        this.renewContract = renewContract;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOppId() {
        return oppId;
    }

    public void setOppId(int oppId) {
        this.oppId = oppId;
    }

    public String getTechnicalSkill() {
        return technicalSkill;
    }

    public void setTechnicalSkill(String technicalSkill) {
        this.technicalSkill = technicalSkill;
    }
}
