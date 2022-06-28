package com.hr.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Function {
	private int id;
	private Integer parentId;
	private String functionCode;
	private String functionName;
	private String path;
	private String icon;
	private Integer isDeleted;
	private Timestamp createdAt;
	private Integer createdBy;
	private Timestamp updatedAt;
	private Integer updatedBy;

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "parent_id")
	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Basic
	@Column(name = "function_code")
	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	@Basic
	@Column(name = "function_name")
	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	@Basic
	@Column(name = "path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Basic
	@Column(name = "icon")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Basic
	@Column(name = "is_deleted")
	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Basic
	@Column(name = "created_at")
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Basic
	@Column(name = "created_by")
	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	@Basic
	@Column(name = "updated_at")
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Basic
	@Column(name = "updated_by")
	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Function function = (Function) o;
		return id == function.id &&
				Objects.equals(parentId, function.parentId) &&
				Objects.equals(functionCode, function.functionCode) &&
				Objects.equals(functionName, function.functionName) &&
				Objects.equals(path, function.path) &&
				Objects.equals(icon, function.icon) &&
				Objects.equals(isDeleted, function.isDeleted) &&
				Objects.equals(createdAt, function.createdAt) &&
				Objects.equals(createdBy, function.createdBy) &&
				Objects.equals(updatedAt, function.updatedAt) &&
				Objects.equals(updatedBy, function.updatedBy);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, functionCode, functionName, path, icon, isDeleted, createdAt, createdBy, updatedAt, updatedBy);
	}
}
