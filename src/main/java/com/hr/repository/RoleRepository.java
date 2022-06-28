package com.hr.repository;

import com.hr.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.isDeleted = 0 AND r.id IN :ids ORDER BY r.createdAt DESC, r.id DESC")
    List<Role> findAllByIdIn(@Param("ids") List<Integer> ids);

    @Query("SELECT r FROM Role r WHERE r.isDeleted = 0 ORDER BY r.createdAt DESC, r.id DESC")
    List<Role> findByIsDeleted();

    Role findAllById(Integer id);

    @Query("SELECT r FROM Role r WHERE lower(r.roleCode) LIKE :roleName" +
            " OR LOWER(r.roleName) LIKE :roleName")
    List<Role> findByRoleCodeOrRoleName(String roleName);

    @Query("SELECT r FROM Role r WHERE lower(r.roleCode) = :roleName" +
            " OR lower(r.roleName) = :roleName")
    List<Role> findByExactRoleCodeOrRoleName(String roleName);
}