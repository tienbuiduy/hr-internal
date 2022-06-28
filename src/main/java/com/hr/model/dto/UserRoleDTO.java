package com.hr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserRoleDTO implements Serializable {
	private int id;
	private int userId;
	private int roleId;
	private boolean isDeleted;

	@JsonProperty
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty("user_id")
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	@JsonProperty("role_id")
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@JsonProperty("is_deleted")
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
