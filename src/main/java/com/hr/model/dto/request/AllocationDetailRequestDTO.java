package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

public class AllocationDetailRequestDTO {
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp startDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp endDate;
    private String employeeName;
    private String projectName;
    private String pmName;
    private boolean checkFreeEmployee;
    private List<Integer> roles;

    public AllocationDetailRequestDTO() {
    }

    public AllocationDetailRequestDTO(Timestamp startDate, Timestamp endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPmName() {
        return pmName;
    }

    public void setPmName(String pmName) {
        this.pmName = pmName;
    }

    public boolean isCheckFreeEmployee() {
        return checkFreeEmployee;
    }

    public void setCheckFreeEmployee(boolean checkFreeEmployee) {
        this.checkFreeEmployee = checkFreeEmployee;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}
