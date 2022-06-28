package com.hr.model.dto.response;

import java.sql.Timestamp;

public class AllocationErrorResponseDTO {
    private int allocationNo;
    private String employeeName;
    private String email;
    private Timestamp startDate;
    private Timestamp endDate;
    private String projectRole;
    private float allo;
    private float rate;
    private String note;
    private String projectName;
    private String source;
    private String message;

    public AllocationErrorResponseDTO() {
    }

    public int getAllocationNo() {
        return allocationNo;
    }

    public void setAllocationNo(int allocationNo) {
        this.allocationNo = allocationNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }

    public float getAllo() {
        return allo;
    }

    public void setAllo(float allo) {
        this.allo = allo;
    }

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
