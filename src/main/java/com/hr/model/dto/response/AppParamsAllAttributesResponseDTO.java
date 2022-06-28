package com.hr.model.dto.response;

public class AppParamsAllAttributesResponseDTO {
    String type;
    String code;
    String name;
    int order;

    public AppParamsAllAttributesResponseDTO() {
    }

    public AppParamsAllAttributesResponseDTO(String type, String code, String name, int order) {
        this.type = type;
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
