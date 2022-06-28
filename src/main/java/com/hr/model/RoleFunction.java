package com.hr.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "role_function")
public class RoleFunction {
	private int id;
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
		RoleFunction that = (RoleFunction) o;
		return id == that.id &&
				Objects.equals(createdAt, that.createdAt) &&
				Objects.equals(createdBy, that.createdBy) &&
				Objects.equals(updatedAt, that.updatedAt) &&
				Objects.equals(updatedBy, that.updatedBy);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdAt, createdBy, updatedAt, updatedBy);
	}
}
