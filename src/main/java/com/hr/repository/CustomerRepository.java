package com.hr.repository;

import com.hr.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.isDeleted = 0 ORDER BY c.createdAt DESC, c.id DESC")
    List<Customer> findActiveCustomer();

    @Modifying
    @Query(value = "UPDATE Customer c SET c.isDeleted = 1, c.updatedBy = :loginUserId WHERE c.id = :id")
    void deleteCustomer(@Param("id") Integer id, Integer loginUserId);

    @Query("SELECT c FROM Customer c WHERE (:customerCode IS NULL OR LOWER(c.customerCode) LIKE %:customerCode%) " +
            "AND (:customerName IS NULL OR LOWER(c.customerName) LIKE %:customerName%)" +
            "AND (:countryCode IS NULL OR LOWER(c.countryCode) LIKE %:countryCode%)" +
            "AND (:rank IS NULL OR LOWER(c.rank) LIKE %:rank%)" +
            "AND (c.isDeleted = 0) ORDER BY c.createdAt DESC, c.id DESC"
    )
    List<Customer> search(@Param("customerCode") String customerCode, @Param("customerName") String customerName, @Param("countryCode") String countryCode, @Param("rank") String rank);

    Customer findFirstByOrderByIdDesc();

    Customer findById(int id);
}
