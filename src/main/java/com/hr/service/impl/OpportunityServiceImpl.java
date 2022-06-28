package com.hr.service.impl;

import com.hr.constant.CommonConstant;
import com.hr.model.ObjectValidation;
import com.hr.model.Opportunity;
import com.hr.model.dto.request.OpportunityRequestDTO;
import com.hr.model.dto.request.OpportunitySearchRequestDTO;
import com.hr.model.dto.response.OpportunityResponseDTO;
import com.hr.repository.OpportunityRepository;
import com.hr.repository.OpportunityRepositoryCustom;
import com.hr.service.OpportunityService;
import com.hr.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
public class OpportunityServiceImpl implements OpportunityService {
    private final String DEFAULT_OPPORTUNITY_CODE = "OPP0001";

    private ModelMapper modelMapper;
    private OpportunityRepository opportunityRepository;
    private OpportunityRepositoryCustom opportunityRepositoryCustom;
    private UserService userService;

    public OpportunityServiceImpl(UserService userService,
                                  OpportunityRepository opportunityRepository, ModelMapper modelMapper, OpportunityRepositoryCustom opportunityRepositoryCustom) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.opportunityRepositoryCustom = opportunityRepositoryCustom;
    }

    public List<OpportunityResponseDTO> getOppotunities() {
        List<OpportunityResponseDTO> opportunityResponseDTOS = new ArrayList<OpportunityResponseDTO>();
        List<String> statuses = new ArrayList<>();
        statuses.add(CommonConstant.OpportunityStatus.NEW);
        statuses.add(CommonConstant.OpportunityStatus.INPROGRESS);
        statuses.add(CommonConstant.OpportunityStatus.SEND_ESTIMATE);
        List<Opportunity> opportunityList = opportunityRepository.findOpportunityByStatus(statuses);

        opportunityList.forEach(opportunity -> {
            OpportunityResponseDTO oppDTO = toResponseDTO(opportunity);
            opportunityResponseDTOS.add(oppDTO);
        });
        return opportunityResponseDTOS;
    }

    @Override
    public Opportunity createOrUpdate(OpportunityRequestDTO opportunityRequestDTO) {
        return opportunityRepository.save(toOpportunity(opportunityRequestDTO));
    }

    @Override
    public void delete(int id, int loginUserId) {
        opportunityRepository.deleteOpportunity(id, loginUserId);
    }

    @Override
    public void updateOpportunityStatus(int id, String status) {
        opportunityRepository.updateOpportunityStatus(id, status);
    }

    @Override
    public List<ObjectValidation> validate(OpportunityRequestDTO opportunityRequestDTO) {
        List<ObjectValidation> objectValidationList = new ArrayList<>();
        String opportunityName = opportunityRequestDTO.getOppName();
        String opportunityNameExist = opportunityRepository.opportunityNameExist(opportunityName.toLowerCase());
        if (!Objects.isNull(opportunityNameExist) && !opportunityNameExist.isEmpty()) {
            objectValidationList.add(new ObjectValidation("oppName", CommonConstant.ErrorMessage.OPPORTUNITY_NAME_IS_EXIST));
        }
        return objectValidationList;
    }

    @Override
    public List<OpportunityResponseDTO> search(OpportunitySearchRequestDTO opportunitySearchRequestDTO) {
        String oppName = opportunitySearchRequestDTO.getOppName().toLowerCase().trim();
        int contractTypeId = opportunitySearchRequestDTO.getContractTypeId();
        Timestamp fromStartDate = minusADayToTimeStamp(opportunitySearchRequestDTO.getFromStartDate());
        Timestamp toStartDate = plusADayToTimeStamp(opportunitySearchRequestDTO.getToStartDate());
        Timestamp fromEndDate = minusADayToTimeStamp(opportunitySearchRequestDTO.getFromEndDate());
        Timestamp toEndDate = plusADayToTimeStamp(opportunitySearchRequestDTO.getToEndDate());
        int tempPm = opportunitySearchRequestDTO.getTempPm();
        int planPm = opportunitySearchRequestDTO.getPlanPm();
        List<String> listStatus = opportunitySearchRequestDTO.getStatus();

        List<OpportunityResponseDTO> opportunityList = opportunityRepositoryCustom.search(oppName, contractTypeId,
                planPm, tempPm, fromStartDate, toStartDate, fromEndDate, toEndDate, listStatus);

        return opportunityList;
    }

    @Override
    public synchronized String generateCode() {
        Opportunity lastOpportunity = opportunityRepository.findFirstByOrderByIdDesc();
        if (lastOpportunity == null) {
            return DEFAULT_OPPORTUNITY_CODE;
        }
        String code = "OPP" + String.format("%04d", lastOpportunity.getId() + 1);
        return code;
    }

    public OpportunityResponseDTO toResponseDTO(Opportunity opportunity) {
        OpportunityResponseDTO opportunityResponseDTO = modelMapper.map(opportunity, OpportunityResponseDTO.class);
        opportunityResponseDTO.setStatusString(opportunityRepository.getStatusNameFromCode(opportunity.getStatus(), CommonConstant.AppParams.OPPORTUNITY_STATUS));
        return opportunityResponseDTO;
    }

    private Opportunity toOpportunity(OpportunityRequestDTO opportunityRequestDTO) {
        Opportunity opportunity = modelMapper.map(opportunityRequestDTO, Opportunity.class);

        int loginUserId = userService.getLoginUserId();
        if(opportunityRequestDTO.getId() == 0){
            opportunity.setCreatedBy(loginUserId);
        } else {
            Opportunity opportunityOldData = opportunityRepository.findById(opportunityRequestDTO.getId());
            opportunity.setCreatedBy(opportunityOldData.getCreatedBy());
        }
        opportunity.setUpdatedBy(loginUserId);
        return opportunity;
    }

    private Timestamp plusADayToTimeStamp(Timestamp timestamp) {
        if (timestamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timestamp);
            cal.add(Calendar.DAY_OF_WEEK, 1);
            timestamp.setTime(cal.getTime().getTime());
            timestamp = new Timestamp(cal.getTime().getTime());
            return timestamp;
        }
        return timestamp;
    }

    private Timestamp minusADayToTimeStamp(Timestamp timestamp) {
        if (timestamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timestamp);
            cal.add(Calendar.DAY_OF_WEEK, -1);
            timestamp.setTime(cal.getTime().getTime());
            timestamp = new Timestamp(cal.getTime().getTime());
            return timestamp;
        }
        return timestamp;
    }
}
