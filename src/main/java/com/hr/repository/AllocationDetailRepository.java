package com.hr.repository;

import com.hr.model.AllocationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AllocationDetailRepository extends JpaRepository<AllocationDetail, Integer> {
    @Modifying
    @Query(value = "DELETE FROM AllocationDetail WHERE allo_id = :allocationId")
    void deleteByAllocationId(@Param("allocationId") Integer allocationId);

    @Modifying
    @Query(value = "UPDATE AllocationDetail a SET a.project.id = :projectId, a.opportunity.id = null WHERE a.opportunity.id = :oppId")
    void updateAllocationDetailOfOpportunityToProject(Integer oppId, Integer projectId);
}
