package com.hr.service;

import com.hr.model.AppParams;
import com.hr.model.dto.response.AppParamsAllAttributesResponseDTO;
import com.hr.model.dto.response.AppParamsResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface AppParamsService {
    default List<AppParamsResponseDTO> list() {
        List<AppParams> appParamsList = getAll();
        return appParamsList.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    default List<AppParamsAllAttributesResponseDTO> listAllAttributes() {
        List<AppParams> appParamsList = getAll();
        return appParamsList.stream().map(this::toAllAttributeResponseDTO).collect(Collectors.toList());
    }

    List<AppParams> getAll();

    AppParamsResponseDTO toResponseDTO(AppParams appParams);

    AppParamsAllAttributesResponseDTO toAllAttributeResponseDTO(AppParams appParams);

    List<AppParamsResponseDTO> listAppParamsByType(String type);
}
