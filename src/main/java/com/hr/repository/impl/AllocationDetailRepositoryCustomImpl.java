package com.hr.repository.impl;

import com.hr.model.AllocationDetail;
import com.hr.repository.AllocationDetailRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Objects;

@Repository
public class AllocationDetailRepositoryCustomImpl implements AllocationDetailRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveAllocationDetail(AllocationDetail allocationDetail) {
        String query = "";
        if(Objects.isNull(allocationDetail.getProject())){
            query =
                    "INSERT INTO {h-schema}allocation_detail(id, allo_id, employee_id, role_id, allo, opp_id, allo_date, created_at, created_by, updated_at, updated_by)" +
                            "   SELECT" +
                            "   nextval('{h-schema}allocation_detail_id_seq')," +
                            "   :alloId," +
                            "   :empoyeeId," +
                            "   :roleId," +
                            "   :allo," +
                            "   :oppId," +
                            "   generate_series(cast (:startDate as TIMESTAMP), cast (:endDate as TIMESTAMP), '1 day')," +
                            "   now()," +
                            "   :createdBy," +
                            "   now()," +
                            "   :updatedBy";
        }
        else{
            query =
                    "INSERT INTO {h-schema}allocation_detail(id, allo_id, employee_id, role_id, allo, project_id, allo_date, created_at, created_by, updated_at, updated_by)" +
                            "   SELECT" +
                            "   nextval('{h-schema}allocation_detail_id_seq')," +
                            "   :alloId," +
                            "   :empoyeeId," +
                            "   :roleId," +
                            "   :allo," +
                            "   :projectId," +
                            "   generate_series(cast (:startDate as TIMESTAMP), cast (:endDate as TIMESTAMP), '1 day')," +
                            "   now()," +
                            "   :createdBy," +
                            "   now()," +
                            "   :updatedBy";
        }

        Query q = entityManager.createNativeQuery(query, AllocationDetail.class);
        q.setParameter("alloId", allocationDetail.getAllocation().getId());
        q.setParameter("empoyeeId", allocationDetail.getUser().getId());
        q.setParameter("roleId", allocationDetail.getRole().getId());
        q.setParameter("allo", allocationDetail.getAllo());
        if(!Objects.isNull(allocationDetail.getOpportunity())){
            q.setParameter("oppId", allocationDetail.getOpportunity().getId());
        }
        if(!Objects.isNull(allocationDetail.getProject())){
            q.setParameter("projectId", allocationDetail.getProject().getId());
        }
        q.setParameter("startDate", allocationDetail.getAllocation().getStartDate(), TemporalType.TIMESTAMP);
        q.setParameter("endDate", allocationDetail.getAllocation().getEndDate(), TemporalType.TIMESTAMP);
        q.setParameter("createdBy", allocationDetail.getCreatedBy());
        q.setParameter("updatedBy", allocationDetail.getUpdatedBy());
        q.executeUpdate();
    }
}
