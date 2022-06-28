package com.hr.service;

import com.hr.model.Role;
import com.hr.model.dto.RoleDTO;

import java.util.ArrayList;
import java.util.List;

public interface RoleService {
	List<Role> findByIds(List<Integer> ids);

	List<Role> findByIsDeleted();

	default List<RoleDTO> getRoles() {
		List<Role> roles = findByIsDeleted();
		List<RoleDTO> roleDTOS = toListRoleDTO(roles);
		return roleDTOS;
	}

	default List<RoleDTO> toListRoleDTO(List<Role> roles){
		List<RoleDTO> roleDTOS = new ArrayList<>();
		for (Role r : roles) {
			RoleDTO roleDTO = toDto(r);
			roleDTOS.add(roleDTO);
		}
		return roleDTOS;
	}

	default RoleDTO toDto(Role role) {
		RoleDTO dto = new RoleDTO();
		dto.setId(role.getId());
		dto.setRoleCode(role.getRoleCode());
		dto.setRoleName(role.getRoleName());
		dto.setIsDeleted(role.getIsDeleted());
		return dto;
	}
}
