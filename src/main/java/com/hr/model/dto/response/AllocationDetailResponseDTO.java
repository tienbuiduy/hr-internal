package com.hr.model.dto.response;

import java.sql.Timestamp;

public class AllocationDetailResponseDTO {
    private int employeeId;
    private String employeeName;
    private int projectId;
    private String projectName;
    private String roleName;
    private String pmName;
    private Timestamp startDate; // start date project
    private Timestamp endDate;
    private String status;
    private String mainSkill;

    public AllocationDetailResponseDTO() {
    }

    public AllocationDetailResponseDTO(int employeeId, String employeeName, int projectId, String projectName, String roleName,
                                       String pmName, Timestamp startDate, Timestamp endDate, String status, String mainSkill) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.roleName = roleName;
        this.pmName = pmName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.mainSkill = mainSkill;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
    }

    @Override
    public String toString() {
        return "AllocationDetailResponseDTO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", pmName='" + pmName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", mainSkill='" + mainSkill + '\'' +
                '}';
    }
}
