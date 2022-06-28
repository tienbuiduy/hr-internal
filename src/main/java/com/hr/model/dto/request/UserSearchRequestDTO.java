package com.hr.model.dto.request;

import java.util.List;

public class UserSearchRequestDTO {
	private String employeeCode;
	private String employeeName;
	private String email;
	private String mainSkill;
	private String groupName;
	private List<Integer> roles;
	private List<String> status;
	private String department;

	public UserSearchRequestDTO() {
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
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

	public String getMainSkill() {
		return mainSkill;
	}

	public void setMainSkill(String mainSkill) {
		this.mainSkill = mainSkill;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
