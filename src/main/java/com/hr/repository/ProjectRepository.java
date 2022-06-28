package com.hr.repository;

import com.hr.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT a.name FROM AppParams a WHERE a.type = 'PROJECT_RANK_TYPE' ORDER BY a.name ASC")
    List<String> getListRank();

    @Query("SELECT p FROM Project p WHERE p.isDeleted = 0 ORDER BY p.createdAt DESC, p.id DESC")
    List<Project> findActiveProject();

    @Modifying
    @Query(value = "UPDATE Project o SET o.isDeleted = 1, o.updatedBy = :loginUserId WHERE o.id = :id")
    void deleteProject(@Param("id") Integer id, Integer loginUserId);

    Project findAllById(Integer id);

    @Query("SELECT p FROM Project p WHERE (:projectName IS NULL OR LOWER(p.projectName) LIKE %:projectName%) " +
            "AND (:contractTypeId = 0 OR p.contractTypeId = :contractTypeId)" +
            "AND (:rankCode IS NULL OR p.rank LIKE %:rankCode%)" +
            "AND (:customerId = 0 OR p.customerId = :customerId)" +
            "AND (:pmId = 0 OR p.pmId = :pmId)" +
            "AND (:status IS NULL OR p.status LIKE %:status%)" +
            "AND (:technicalSkill IS NULL OR LOWER(p.technicalSkill) LIKE %:technicalSkill%)" +
            "AND (p.isDeleted = 0)" +
            "ORDER BY p.createdAt DESC, p.id DESC"
    )
    List<Project> search(@Param("projectName") String projectName, @Param("contractTypeId") int contractTypeId,
                         @Param("rankCode") String rankCode, @Param("customerId") int customerId, @Param("pmId") int pmId,
                         @Param("status") String status, @Param("technicalSkill") String technicalSkill);

    @Query("SELECT a.name FROM AppParams a WHERE a.code = :rankCode AND a.type = :type")
    String getRankNameByRankCode(String rankCode, String type);

    Project findFirstByOrderByIdDesc();

    @Query("SELECT p FROM Project p WHERE LOWER(p.projectName) LIKE %:projectName%")
    List<Project> findByProjectName(@Param("projectName") String projectName);

    @Query("SELECT p FROM Project p WHERE LOWER(p.projectName) = :projectName")
    List<Project> findByExactProjectName(@Param("projectName") String projectName);

    List<Project> findAllByPmId(int pmId);

    Project findById(int id);
}
