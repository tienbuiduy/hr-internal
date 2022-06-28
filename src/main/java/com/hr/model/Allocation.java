package com.hr.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "allocation")
public class Allocation extends AbstractAuditEntity{
	private User user;
	private Project project;
	private Opportunity opportunity;
	private Role role;
	private float allo;
	private Timestamp startDate;
	private Timestamp endDate;
	private int isDeleted = 0;
	private float rate;
	private String note;
	private String source;
	private String type;

	public Allocation(){

	}

	public Allocation(User user, Project project, Opportunity opportunity, Role role, float allo, Timestamp startDate,
			Timestamp endDate, float rate, String note, String source, String type) {
		this.user = user;
		this.project = project;
		this.opportunity = opportunity;
		this.role = role;
		this.allo = allo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.rate = rate;
		this.note = note;
		this.source = source;
		this.type = type;
	}

	@OneToOne
	@JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToOne
	@JoinColumn(name = "project_id", referencedColumnName = "id", nullable = true)
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@OneToOne
	@JoinColumn(name = "opp_id", referencedColumnName = "id", nullable = true)
	public Opportunity getOpportunity() {
		return opportunity;
	}

	public void setOpportunity(Opportunity opportunity) {
		this.opportunity = opportunity;
	}

	@OneToOne
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

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
