package com.hr.repository;

import com.hr.model.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractTypeRepository extends JpaRepository<ContractType, Integer> {
    @Query("SELECT o FROM ContractType o WHERE o.isDeleted = 0 ORDER BY o.createdAt DESC, o.id DESC")
    List<ContractType> findActiveContractType();
}
