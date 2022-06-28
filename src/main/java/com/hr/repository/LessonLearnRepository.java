package com.hr.repository;

import com.hr.model.LessonLearn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface LessonLearnRepository extends JpaRepository<LessonLearn, Integer> {
    @Query("SELECT l FROM LessonLearn l WHERE l.isDeleted = 0 ORDER BY l.createdAt DESC, l.id DESC")
    List<LessonLearn> getListLessonLearn();

    @Modifying
    @Query(value = "UPDATE LessonLearn l SET l.isDeleted = 1, l.updatedBy = :loginUserId, l.updatedAt = :timestampNow WHERE l.id = :id")
    void deleteLessonLearn(@Param("id") Integer id, Integer loginUserId, Timestamp timestampNow);

    @Query("SELECT u FROM LessonLearn u WHERE (:description IS NULL OR LOWER(u.description) LIKE %:description%) " +
            "AND (:category IS NULL OR LOWER(u.category) LIKE %:category%)" +
            "AND (:impact IS NULL OR LOWER(u.impact) LIKE %:impact%)" +
            "AND (:rootCause IS NULL OR LOWER(u.rootCause) LIKE %:rootCause%)" +
            "AND (:correctiveAction IS NULL OR LOWER(u.correctiveAction) LIKE %:correctiveAction%)" +
            "AND (:expectResult IS NULL OR LOWER(u.expectResult) LIKE %:expectResult%)" +
            "AND (:otherNotes IS NULL OR LOWER(u.otherNotes) LIKE %:otherNotes%)" +
            "AND (:problemSource IS NULL OR LOWER(u.problemSource) LIKE %:problemSource%)" +
            "AND (:longTermSolution IS NULL OR LOWER(u.longTermSolution) LIKE %:longTermSolution%)" +
            "AND (u.isDeleted = 0)" +
            "ORDER BY u.createdAt DESC, u.id DESC"
    )
    List<LessonLearn> search(String description, String category, String impact,
                      String rootCause, String correctiveAction, String expectResult,
                      String otherNotes, String problemSource, String longTermSolution);

    @Query("SELECT u FROM LessonLearn u WHERE (LOWER(u.description) LIKE %:description%) " +
            "AND (LOWER(u.rootCause) LIKE %:rootCause%)" +
            "AND (LOWER(u.correctiveAction) LIKE %:correctiveAction%)" +
            "AND (LOWER(u.projectName) LIKE %:projectName%)" +
            "AND (u.isDeleted = 0)" +
            "ORDER BY u.createdAt DESC, u.id DESC"
    )
    List<LessonLearn> findLessonLearnExists(String description, String rootCause, String correctiveAction, String projectName);
}
