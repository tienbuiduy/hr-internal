package com.hr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role extends AbstractAuditEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "role_code", unique = true, nullable = false)
	private String roleCode;

	@Column(name = "role_name", unique = true, nullable = false)
	private String roleName;

	@Column(name = "is_deleted")
	private Integer isDeleted = 0;

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
}
