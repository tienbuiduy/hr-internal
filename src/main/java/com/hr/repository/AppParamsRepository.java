package com.hr.repository;

import com.hr.model.AppParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppParamsRepository extends JpaRepository<AppParams, Integer> {
    @Query("SELECT a FROM AppParams a WHERE a.type = :type ORDER BY a.order ASC")
    List<AppParams> getListEmployeeStatus(String type);
}
