package com.hr.service;

import com.hr.model.ObjectValidation;
import com.hr.model.Opportunity;
import com.hr.model.dto.request.OpportunityRequestDTO;
import com.hr.model.dto.request.OpportunitySearchRequestDTO;
import com.hr.model.dto.response.OpportunityResponseDTO;

import java.util.List;

public interface OpportunityService {
    List<OpportunityResponseDTO> getOppotunities();

    Opportunity createOrUpdate(OpportunityRequestDTO opportunityRequestDTO) throws Exception;

    void delete(int id, int loginUserId);

    List<OpportunityResponseDTO> search(OpportunitySearchRequestDTO opportunitySearchRequestDTO);

    String generateCode();

    void updateOpportunityStatus(int opportunityId, String status);

    List<ObjectValidation> validate(OpportunityRequestDTO opportunityRequestDTO);
}
