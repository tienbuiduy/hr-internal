package com.hr.repository;

import com.hr.model.User;
import com.hr.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findAllByUser(User user);

    void deleteAllByUser(User user);
}
