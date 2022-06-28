package com.hr.service;

import com.hr.model.Role;
import com.hr.model.User;
import com.hr.model.UserRole;

import java.util.List;
import java.util.stream.Collectors;

public interface UserRoleService {

    default void save(User user, List<Role> roles) {
        List<UserRole> userRoles = roles.stream().map(role -> new UserRole().user(user).role(role)).collect(Collectors.toList());
        saveAll(userRoles);
    }

    void saveAll(List<UserRole> userRoles);

    void deleteByUser(User user);

    List<UserRole> findAllByUser(User user);
}
