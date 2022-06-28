package com.hr.repository;

import com.hr.model.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Integer> {
    Opportunity findFirstByOrderByIdDesc();

    Opportunity findAllById(Integer id);

    @Query("SELECT o FROM Opportunity o WHERE o.isDeleted = 0 AND o.status IN :statuses ORDER BY o.createdAt DESC, o.id DESC")
    List<Opportunity> findOpportunityByStatus(List<String> statuses);

    @Modifying
    @Query(value = "UPDATE Opportunity o SET o.isDeleted = 1, o.updatedBy = :loginUserId WHERE o.id = :id")
    void deleteOpportunity(@Param("id") Integer id, Integer loginUserId);

    @Modifying
    @Query(value = "UPDATE Opportunity o SET o.status = :status WHERE o.id = :id")
    void updateOpportunityStatus(@Param("id") Integer id, String status);

    @Modifying
    @Query(value = "UPDATE Opportunity o SET o.isDeleted = 1 WHERE o.oppName = :oppName")
    void deleteOpportunityByName(@Param("oppName") String oppName);

    @Query(value = "SELECT a.name FROM AppParams a WHERE a.code = :code and a.type = :type")
    String getStatusNameFromCode(String code, String type);

    @Query("SELECT o.oppName FROM Opportunity o WHERE LOWER(o.oppName) LIKE %:oppName% AND o.isDeleted = 0")
    String opportunityNameExist(String oppName);

    @Query("SELECT o FROM Opportunity o WHERE LOWER(o.oppName) = :oppName")
    Opportunity findByExactOpportunityName(String oppName);

    Opportunity findById(int id);
}
