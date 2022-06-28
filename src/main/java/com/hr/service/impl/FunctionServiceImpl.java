package com.hr.service.impl;

import com.hr.model.dto.FunctionDTO;
import com.hr.repository.FunctionRepositoryCustom;
import com.hr.service.FunctionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionServiceImpl implements FunctionService {
    private FunctionRepositoryCustom functionRepositoryCustom;

    public FunctionServiceImpl(FunctionRepositoryCustom functionRepositoryCustom) {
        this.functionRepositoryCustom = functionRepositoryCustom;
    }

    @Override
    public List<FunctionDTO> getFunctionByUserId(Integer userId) {
        return functionRepositoryCustom.getFunctionByUserId(userId);
    }

    @Override
    public boolean checkRoleUser(Integer userId, String urlPath) {
        return functionRepositoryCustom.checkRoleUser(userId, urlPath);
    }
}
