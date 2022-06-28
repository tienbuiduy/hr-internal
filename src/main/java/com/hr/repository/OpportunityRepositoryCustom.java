package com.hr.repository;

import com.hr.model.dto.response.OpportunityResponseDTO;

import java.sql.Timestamp;
import java.util.List;

public interface OpportunityRepositoryCustom {
    List<OpportunityResponseDTO> search(String oppName, int contractTypeId, int planPm, int tempPm, Timestamp fromStartDate,
                                        Timestamp toStartDate, Timestamp fromEndDate, Timestamp toEndDate, List<String> listStatus);
}
