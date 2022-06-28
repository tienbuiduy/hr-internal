package com.hr.service;

import com.hr.model.ObjectValidation;
import com.hr.model.dto.request.AllocationDetailRequestDTO;
import com.hr.model.dto.request.AllocationRequestDTO;
import com.hr.model.dto.request.AllocationSearchRequestDTO;
import com.hr.model.dto.request.EERequestDTO;
import com.hr.model.dto.response.AllocationDetailResponseDTO;
import com.hr.model.dto.response.AllocationEmployeeResponseDTO;
import com.hr.model.dto.response.AllocationResponseDTO;
import com.hr.model.dto.response.EEResponseDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

public interface AllocationService {
    List<AllocationResponseDTO> list();

    void save(AllocationRequestDTO allocationRequestDTO);

    void delete(int id, int loginUserId);

    List<AllocationResponseDTO> search(AllocationSearchRequestDTO allocationSearchRequestDTO);

    List<AllocationDetailResponseDTO> getListAllocationDetail(AllocationDetailRequestDTO allocationDetailRequestDTO);

    List<AllocationDetailResponseDTO> getListFreeAllocationDetail(AllocationDetailRequestDTO allocationDetailRequestDTO);

    List<AllocationEmployeeResponseDTO> getListAllocationEmployee(AllocationDetailRequestDTO allocationDetailRequestDTO);

    void saveAllocationAndDetail(AllocationRequestDTO allocationRequestDTO);

    void updateAllocationOfOpportunityToProject(int oppId, int projectId);

    boolean isDuplicateAllocation(int employeeId, int projectId, Timestamp startDate, Timestamp endDate);

    String importAllocation(MultipartFile file) throws IOException;

    String importAllocationByMonth(MultipartFile file) throws IOException, ParseException;

    String importAllocationByDay(MultipartFile file) throws IOException, ParseException;

    String importAllocationByPeriod(MultipartFile file) throws IOException, ParseException;

    ByteArrayOutputStream exportEE(EERequestDTO eERequestDTO) throws IOException, InvalidFormatException;

    List<EEResponseDTO> getListEE(EERequestDTO eERequestDTO);

    List<ObjectValidation> checkConditionToExportEE();

    String checkConditionToImportAllocation();

    ByteArrayOutputStream exportAllocationByDay(List<AllocationResponseDTO> allocationResponseDTOS) throws IOException;

    ByteArrayOutputStream exportAllocationByMonth(List<AllocationResponseDTO> allocationResponseDTOS) throws IOException;

    ByteArrayOutputStream exportAllocationByPeriod(List<AllocationResponseDTO> allocationResponseDTOS) throws IOException;
}
