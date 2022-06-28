package com.hr.repository.impl;

import com.hr.model.dto.FunctionDTO;
import com.hr.repository.FunctionRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


@Repository
public class FunctionRepositoryCustomImpl implements FunctionRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<FunctionDTO> getFunctionByUserId(Integer userId) {
        String query =
                "SELECT\n" +
                        "DISTINCT\tf.parent_id, f.function_code, f.function_name, f.\"path\", f.icon, f.\"order\" \n" +
                        "FROM\n" +
                        "\t{h-schema}\"function\" f\n" +
                        "\tINNER JOIN {h-schema}role_function rf ON f.\"id\" = rf.function_id \n" +
                        "WHERE\n" +
                        "\trf.role_id IN ( SELECT ur.role_id FROM {h-schema}user_role ur INNER JOIN {h-schema}\"user\" u ON ur.user_id = u.\"id\" WHERE u.\"id\" = ? ) \n" +
                        "\tAND is_deleted = 0 \n" +
                        "ORDER BY\n" +
                        "\tf.\"order\"";
        Query q = entityManager.createNativeQuery(query);
        q.setParameter(1, userId);
        List<Object> objects = q.getResultList();
        List<FunctionDTO> functionDTOS = new ArrayList<>();
        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            FunctionDTO functionDTO = new FunctionDTO();
            if (ob[0] != null) {
                functionDTO.setParentId(Integer.valueOf(ob[0].toString()));
            }
            functionDTO.setFunctionCode(ob[1].toString());
            functionDTO.setFunctionName(ob[2].toString());
            functionDTO.setPath(ob[3].toString());
            functionDTO.setIcon(ob[4].toString());
            functionDTO.setOrder(Integer.valueOf(ob[5].toString()));
            functionDTOS.add(functionDTO);
        }
        return functionDTOS;
    }

    @Override
    public boolean checkRoleUser(Integer userId, String urlPath) {
        String query =
                "SELECT\n" +
                        "DISTINCT\tf.parent_id, f.function_code, f.function_name, f.\"path\", f.icon, f.\"order\" \n" +
                        "FROM\n" +
                        "\t{h-schema}\"function\" f\n" +
                        "\tINNER JOIN {h-schema}role_function rf ON f.\"id\" = rf.function_id \n" +
                        "WHERE\n" +
                        "\trf.role_id IN ( SELECT ur.role_id FROM {h-schema}user_role ur INNER JOIN {h-schema}\"user\" u ON ur.user_id = u.\"id\" WHERE u.\"id\" = :userId ) \n" +
                        "\tAND is_deleted = 0 \n" +
                        "\tAND f.\"path\"=:urlPath \n" +
                        "ORDER BY\n" +
                        "\tf.\"order\"";
        Query q = entityManager.createNativeQuery(query);
        q.setParameter("userId", userId);
        q.setParameter("urlPath", urlPath);
        List<Object> objects = q.getResultList();
        return !CollectionUtils.isEmpty(objects);
    }

}
