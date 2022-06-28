package com.hr.service.impl;

import com.hr.model.AppParams;
import com.hr.model.dto.response.AppParamsAllAttributesResponseDTO;
import com.hr.model.dto.response.AppParamsResponseDTO;
import com.hr.repository.AppParamsRepository;
import com.hr.service.AppParamsService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)

public class AppParamsServiceImpl implements AppParamsService {
    private ModelMapper modelMapper;

    private AppParamsRepository appParamsRepository;

    public AppParamsServiceImpl(AppParamsRepository appParamsRepository, ModelMapper modelMapper) {
        this.appParamsRepository = appParamsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AppParams> getAll() {
        return appParamsRepository.findAll();
    }

    @Override
    public AppParamsResponseDTO toResponseDTO(AppParams appParams) {
        AppParamsResponseDTO appParamsResponseDTO = modelMapper.map(appParams, AppParamsResponseDTO.class);
        return appParamsResponseDTO;
    }

    @Override
    public AppParamsAllAttributesResponseDTO toAllAttributeResponseDTO(AppParams appParams) {
        AppParamsAllAttributesResponseDTO appParamsResponseDTO = modelMapper.map(appParams, AppParamsAllAttributesResponseDTO.class);
        return appParamsResponseDTO;
    }

    @Override
    public List<AppParamsResponseDTO> listAppParamsByType(String type) {
        List<AppParams> appParamsList = appParamsRepository.getListEmployeeStatus(type);
        List<AppParamsResponseDTO> appParamsResponseDTOS = new ArrayList<>();
        for (AppParams appParams : appParamsList) {
            appParamsResponseDTOS.add(modelMapper.map(appParams, AppParamsResponseDTO.class));
        }
        return appParamsResponseDTOS;
    }
}
