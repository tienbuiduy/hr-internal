package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

public class AllocationSearchRequestDTO {
    private String employeeName;
    private List<Integer> roles;
    private String projectName;
    private String pmName;
    private float startRate;
    private float endRate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp fromStartDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp toStartDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp fromEndDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp toEndDate;
    private List<String> type;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
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

    public float getStartRate() {
        return startRate;
    }

    public void setStartRate(float startRate) {
        this.startRate = startRate;
    }

    public float getEndRate() {
        return endRate;
    }

    public void setEndRate(float endRate) {
        this.endRate = endRate;
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

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }
}
