package com.hr.repository;

import java.util.List;

public interface UserRepositoryCustom {
    List<Integer> findRoleIdByUserId(int userId);
}
