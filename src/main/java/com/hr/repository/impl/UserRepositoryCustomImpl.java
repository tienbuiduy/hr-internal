package com.hr.repository.impl;

import com.hr.repository.UserRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Integer> findRoleIdByUserId(int userId) {
        String query =
                "SELECT " +
                        "DISTINCT r.id " +
                        "FROM " +
                        "{h-schema}\"user\" AS u " +
                        "INNER JOIN {h-schema}user_role AS ur ON u.ID = ur.user_id " +
                        "INNER JOIN {h-schema}\"role\" AS r ON ur.role_id = r.id " +
                        "WHERE " +
                        "u.id = ? ";
        Query q = entityManager.createNativeQuery(query);
        q.setParameter(1, userId);
        List<Object> objects = q.getResultList();
        return q.getResultList();
    }
}
