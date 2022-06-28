package com.hr.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Objects;

public class AllocationEmployeeRequestDTO {
    private int employeeId;
    private int projectId;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp startDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp endDate;

    public AllocationEmployeeRequestDTO() {
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationEmployeeRequestDTO that = (AllocationEmployeeRequestDTO) o;
        return employeeId == that.employeeId &&
                projectId == that.projectId &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, projectId, startDate, endDate);
    }
}
