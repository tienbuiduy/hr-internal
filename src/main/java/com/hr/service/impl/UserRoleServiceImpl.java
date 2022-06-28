package com.hr.service.impl;

import com.hr.model.User;
import com.hr.model.UserRole;
import com.hr.repository.UserRoleRepository;
import com.hr.service.UserRoleService;
import com.hr.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository repository;

    private final UserService userService;

    public UserRoleServiceImpl(UserRoleRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public void saveAll(List<UserRole> userRoles) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int loginUserId = userService.getLoginUserId();
        for(UserRole userRole: userRoles){
            userRole.setCreatedAt(now);
            userRole.setCreatedBy(loginUserId);
            userRole.setUpdatedAt(now);
            userRole.setUpdatedBy(loginUserId);
        }
        repository.saveAll(userRoles);
    }

    @Override
    public void deleteByUser(User user) {
        repository.deleteAllByUser(user);
    }

    @Override
    public List<UserRole> findAllByUser(User user) {
        return repository.findAllByUser(user);
    }
}
