package com.hr.repository;

import com.hr.model.Role;
import com.hr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.isDeleted = 0 AND u.status = :status ORDER BY u.createdAt DESC, u.id DESC")
    List<User> findUserByStatus(String status);

    User findAllById(Integer id);

    User findByEmailAndIsDeleted(String email, int isDeleted);

    @Query("SELECT u FROM User u WHERE (:employeeName = '' OR LOWER(u.employeeName) LIKE :employeeName)" +
            "AND (:employeeCode = '' OR LOWER(u.employeeCode) LIKE :employeeCode)" +
            "AND (u.isDeleted = 0)" +
            "ORDER BY u.createdAt DESC, u.id DESC"
    )
    List<User> findUserByEmployeeCodeAndEmployeeName(String employeeCode, String employeeName);

    @Query("SELECT u FROM User u " +
            "WHERE u.employeeName = :employeeName " +
            "AND (u.isDeleted = 0)"
    )
    List<User> findUserByEmployeeName(String employeeName);

    @Modifying
    @Query(value = "UPDATE User u SET u.isDeleted = 1, u.updatedBy = :loginUserId WHERE u.id = :id")
    void deleteUser(@Param("id") Integer id, Integer loginUserId);

    @Query("SELECT u FROM User u WHERE (:email IS NULL OR LOWER(u.email) LIKE %:email%) " +
            "AND (:employeeCode IS NULL OR LOWER(u.employeeCode) LIKE %:employeeCode%)" +
            "AND (:employeeName IS NULL OR LOWER(u.employeeName) LIKE %:employeeName%)" +
            "AND (:mainSkill IS NULL OR LOWER(u.mainSkill) LIKE %:mainSkill%)" +
            "AND (:groupName IS NULL OR LOWER(u.groupName) LIKE %:groupName%)" +
            "AND (:department IS NULL OR LOWER(u.department) LIKE %:department%)" +
            "AND (u.isDeleted = 0)" +
            "ORDER BY u.createdAt DESC, u.id DESC"
    )
    List<User> search(@Param("email") String email, @Param("employeeCode") String employeeCode,
                      @Param("employeeName") String employeeName, @Param("mainSkill") String mainSkill,
                      @Param("groupName") String groupName, @Param("department") String department);

    @Query("SELECT u FROM User u INNER JOIN UserRole ur ON u.id = ur.user.id " +
            "WHERE LOWER(ur.role.roleCode) = :roleCode " +
            "AND (u.isDeleted = 0)" +
            "ORDER BY u.createdAt DESC,  u.id DESC"
    )
    List<User> findAllByRoleCode(@Param("roleCode") String roleCode);

    User findFirstByOrderByIdDesc();

    @Query("SELECT a.name FROM AppParams a WHERE a.type = :type and a.code = :statusCode")
    String getStatusString(String type, String statusCode);

    @Query("SELECT a.name FROM AppParams a WHERE a.code = :status and a.type = :type")
    String getStatusName(String status, String type);

    @Query("SELECT u.email FROM User u WHERE u.email = :email AND u.id <> :id")
    String emailExist(String email, int id);

    @Query("SELECT r FROM User u INNER JOIN UserRole ur ON u.id = ur.user.id INNER JOIN Role r ON ur.role.id = r.id WHERE u.id = :employeeId")
    List<Role> findRolesByUserId(int employeeId);

    @Query("SELECT u from User u where u.employeeCode like %:employeeCode% AND u.isDeleted = 0")
    User findUserByEmployeeCode(String employeeCode);

    User findById(int id);


    @Query("SELECT u.employeeCode FROM User u WHERE u.id = :employeeId")
    String getEmployeeCodeById(int employeeId);

}

