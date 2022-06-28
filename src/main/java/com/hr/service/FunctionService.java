package com.hr.service;

import com.hr.model.dto.FunctionDTO;

import java.util.List;

public interface FunctionService {
    List<FunctionDTO> getFunctionByUserId(Integer userId);

    boolean checkRoleUser(Integer userId, String urlPath);
}
