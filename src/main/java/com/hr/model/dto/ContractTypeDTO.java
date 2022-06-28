package com.hr.model.dto;

import java.io.Serializable;

public class ContractTypeDTO implements Serializable {
	private Integer id;
	private String name;

	public ContractTypeDTO() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
