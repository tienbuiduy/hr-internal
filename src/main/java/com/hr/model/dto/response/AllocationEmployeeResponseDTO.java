package com.hr.model.dto.response;

import java.sql.Timestamp;

public class AllocationEmployeeResponseDTO {
    private int employeeId;
    private String employeeName;
    private int projectId;
    private String projectName;
    private int pmId;
    private String pmName;
    private Timestamp startDate; // start date allocation
    private Timestamp endDate;
    float allocation;

    public AllocationEmployeeResponseDTO() {
    }

    public AllocationEmployeeResponseDTO(int employeeId, String employeeName, int projectId, String projectName, int pmId, String pmName, Timestamp startDate, Timestamp endDate, float allocation) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.pmId = pmId;
        this.pmName = pmName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allocation = allocation;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getPmId() {
        return pmId;
    }

    public void setPmId(int pmId) {
        this.pmId = pmId;
    }

    public String getPmName() {
        return pmName;
    }

    public void setPmName(String pmName) {
        this.pmName = pmName;
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

    public float getAllocation() {
        return allocation;
    }

    public void setAllocation(float allocation) {
        this.allocation = allocation;
    }
}
