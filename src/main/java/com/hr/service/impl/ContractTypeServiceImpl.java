package com.hr.service.impl;

import com.hr.model.ContractType;
import com.hr.model.dto.ContractTypeDTO;
import com.hr.repository.ContractTypeRepository;
import com.hr.service.ContractTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContractTypeServiceImpl implements ContractTypeService {
    private ContractTypeRepository contractTypeRepository;

    public ContractTypeServiceImpl(ContractTypeRepository contractTypeRepository) {
        this.contractTypeRepository = contractTypeRepository;
    }

    @Override
    public List<ContractTypeDTO> getContracts() {
        List<ContractTypeDTO> contractTypes = new ArrayList<ContractTypeDTO>();
        Iterable<ContractType> iterable = contractTypeRepository.findActiveContractType();
        iterable.forEach(contractType -> {
            ContractTypeDTO typeDTO = new ContractTypeDTO();
            typeDTO.setId(contractType.getId());
            typeDTO.setName(contractType.getContractTypeName());
            contractTypes.add(typeDTO);
        });
        return contractTypes;
    }
}
