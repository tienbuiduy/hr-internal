package com.hr.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends AbstractAuditEntity implements Comparable<User> {
    private static final long serialVersionUID = 1L;

    private String employeeCode;
    private String employeeName;
    private String email;
    private String note;
    private String mainSkill;
    private String status; // status code
    private String groupName;
    private int isDeleted = 0;
    private Set<Role> roles = new LinkedHashSet<Role>();
    private String department;
    private Timestamp workingDay;

    @Basic
    @Column(name = "employee_code")
    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    @Basic
    @Column(name = "employee_name")
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    @Column(name = "main_skill")
    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
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
    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
    @Column(name = "department")
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Basic
    @Column(name = "working_day")
    public Timestamp getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Timestamp workingDay) {
        this.workingDay = workingDay;
    }

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public int compareTo(User user) {
        if (user == null) {
            return 1;
        }
        return this.getId() - user.getId();
    }

    public List<Integer> getListRoleId(Set<Role> roles) {
        List<Integer> roleIds = new ArrayList();
        for (Role r : roles) {
            roleIds.add(r.getId());
        }
        return roleIds;
    }

    public List<String> getListRoleCode(Set<Role> roles) {
        List<String> roleCodes = new ArrayList();
        for (Role r : roles) {
            roleCodes.add(r.getRoleCode());
        }
        return roleCodes;
    }
}
