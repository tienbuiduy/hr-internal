package com.hr.repository;

import com.hr.model.Allocation;
import com.hr.model.dto.response.AllocationDetailResponseDTO;
import com.hr.model.dto.response.AllocationEmployeeResponseDTO;
import com.hr.model.dto.response.AllocationResponseDTO;

import java.sql.Timestamp;
import java.util.List;

public interface AllocationRepositoryCustom {
    List<AllocationResponseDTO> search(String employeeName, String projectName, String pmName, Timestamp fromStartDate, Timestamp toStartDate, Timestamp fromEndDate, Timestamp toEndDate, float startRate, float endRate, List<String> types);

    List<AllocationDetailResponseDTO> getListAllocationDetail(String employeeName, String projectName, List<Integer> roleIds, String pmName, Timestamp startDate, Timestamp endDate);

    List<AllocationDetailResponseDTO> getListFreeAllocationDetail(String employeeName, String projectName, List<Integer> roleIds, String pmName, Timestamp startDate, Timestamp endDate);

    List<AllocationEmployeeResponseDTO> getListAllocationEmployee(String projectName, String employeeName);

    List<AllocationResponseDTO> list();

    boolean findAllocationByEmployeeIdAndProjectIdAndStartDateAndEndDate(int employeeId, int projectId, Timestamp startDate, Timestamp endDate);

    boolean findAllocationByTypeAndEmployeeIdAndProjectIdAndStartDateAndEndDate(String type,int employeeId, int projectId, Timestamp startDate, Timestamp endDate);

    List<Allocation> getListAllocationByProjectIdAndTypeAndRoles(int projectId, String type, List<Integer> roles);

}
