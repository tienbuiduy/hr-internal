package com.hr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "contract_type")
public class ContractType extends AbstractAuditEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "contract_type_code")
	private String contractTypeCode;

	@Column(name = "contract_type_name")
	private String contractTypeName;

	@Column(name = "is_deleted")
	private Integer isDeleted;

	public String getContractTypeCode() {
		return contractTypeCode;
	}

	public void setContractTypeCode(String contractTypeCode) {
		this.contractTypeCode = contractTypeCode;
	}

	public String getContractTypeName() {
		return contractTypeName;
	}

	public void setContractTypeName(String contractTypeName) {
		this.contractTypeName = contractTypeName;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
}
