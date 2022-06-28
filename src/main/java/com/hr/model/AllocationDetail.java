package com.hr.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "allocation_detail")
public class AllocationDetail {
    private Integer id;
    private Allocation allocation;
    private User user;
    private Role role;
    private float allo;
    private Project project;
    private Opportunity opportunity;
    private Timestamp alloDate;
    private Timestamp createdAt;
    private Integer createdBy;
    private Timestamp updatedAt;
    private Integer updatedBy;

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "allo_id", referencedColumnName = "id", nullable = false)
    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public float getAllo() {
        return allo;
    }

    public void setAllo(float allo) {
        this.allo = allo;
    }

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @ManyToOne
    @JoinColumn(name = "opp_id", referencedColumnName = "id", nullable = false)
    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public Timestamp getAlloDate() {
        return alloDate;
    }

    public void setAlloDate(Timestamp alloDate) {
        this.alloDate = alloDate;
    }


    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }


    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }
}
