package com.hr.service.impl;

import com.hr.model.Role;
import com.hr.repository.RoleRepository;
import com.hr.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findByIds(List<Integer> ids) {
        return roleRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Role> findByIsDeleted() {
        return roleRepository.findByIsDeleted();
    }


}
