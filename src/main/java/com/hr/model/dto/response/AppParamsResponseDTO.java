package com.hr.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppParamsResponseDTO {
	private String code;
	private String name;

	@JsonProperty
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
