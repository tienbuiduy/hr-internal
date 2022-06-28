package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

public class EERequestDTO {
    private int projectId;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp startDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp endDate;
    private List<Integer> roles;

    public EERequestDTO() {
    }

    public EERequestDTO(int projectId, Timestamp startDate, Timestamp endDate, List<Integer> roles) {
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roles = roles;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}
