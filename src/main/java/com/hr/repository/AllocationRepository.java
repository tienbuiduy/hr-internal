package com.hr.repository;

import com.hr.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Integer> {
    @Query("SELECT a FROM Allocation a WHERE a.isDeleted = 0 GROUP BY a.id, a.user.id, a.project.id ORDER BY a.createdAt DESC, a.id DESC")
    List<Allocation> findActiveAllocation();

    @Modifying
    @Query(value = "UPDATE Allocation a SET a.isDeleted = 1, a.updatedBy = :loginUserId WHERE a.id = :id")
    void delete(@Param("id") Integer id, Integer loginUserId);

    @Modifying
    @Query(value = "UPDATE Allocation a SET a.project.id = :projectId, a.opportunity.id = null WHERE a.opportunity.id = :oppId")
    void updateAllocationOfOpportunityToProject(Integer oppId, Integer projectId);

    @Query("SELECT a FROM Allocation a WHERE a.isDeleted = 0 AND a.project.id = :projectId AND a.role.id IN :roleIds AND a.type = :type ORDER BY a.createdAt DESC, a.id DESC")
    List<Allocation> getListAllocationByProjectIdAndListRoleIdAndType(int projectId, List<Integer> roleIds, String type);

    @Query("SELECT a FROM Allocation a WHERE a.isDeleted = 0 AND a.project.id = :projectId AND a.type = :type ORDER BY a.createdAt DESC, a.id DESC")
    List<Allocation> getListAllocationByProjectIdAndType(int projectId, String type);

    @Query("SELECT CASE WHEN COUNT(h)> 0 THEN TRUE ELSE FALSE END FROM Holiday h WHERE h.year = :year AND h.month = :month AND h.day = :day")
    boolean isHolidayDay(int year, int month, int day);

    @Query("SELECT a FROM Allocation a WHERE a.id = :id")
    Allocation findAllocationById(int id);

    @Query("SELECT a FROM Allocation a WHERE a.user.id = :employeeId AND a.project.id = :projectId AND a.role.id = :roleId AND a.startDate = :startDate AND a.endDate = :endDate")
    List<Allocation> findByEmployeeAndProjectAndRoleAndWorkingDate(int employeeId, int projectId, int roleId, Timestamp startDate, Timestamp endDate);
}
