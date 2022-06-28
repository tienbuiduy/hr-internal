package com.hr.repository;

import com.hr.model.dto.FunctionDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepositoryCustom {
    List<FunctionDTO> getFunctionByUserId(Integer userId);

    boolean checkRoleUser(Integer userId, String urlPath);
}