package com.hr.model.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;

public class OpportunityRequestDTO implements Serializable {
    private int id;
    private String oppCode;
    private String oppName;
    private int contractTypeId;
    private float mm;
    private float totalMm;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp endDate;
    private float duration;
    private float successRate;
    private String note;
    private int tempPmId;
    private int planPmId;
    private String overview;
    private int status;

    public OpportunityRequestDTO() {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
