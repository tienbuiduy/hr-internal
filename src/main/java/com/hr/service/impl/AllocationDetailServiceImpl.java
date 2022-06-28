package com.hr.service.impl;

import com.hr.model.AllocationDetail;
import com.hr.repository.AllocationDetailRepository;
import com.hr.repository.AllocationDetailRepositoryCustom;
import com.hr.service.AllocationDetailService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class AllocationDetailServiceImpl implements AllocationDetailService {
    private AllocationDetailRepository allocationDetailRepository;
    private AllocationDetailRepositoryCustom allocationDetailRepositoryCustom;

    public AllocationDetailServiceImpl(AllocationDetailRepository allocationDetailRepository,
                                       AllocationDetailRepositoryCustom allocationDetailRepositoryCustom) {
        this.allocationDetailRepository = allocationDetailRepository;
        this.allocationDetailRepositoryCustom = allocationDetailRepositoryCustom;
    }

    @Override
    public void deleteByAllocationId(Integer allocationId) {
        allocationDetailRepository.deleteByAllocationId(allocationId);
    }

    @Override
    public void saveAllocationDetail(AllocationDetail allocationDetail) {
        allocationDetailRepositoryCustom.saveAllocationDetail(allocationDetail);
    }
}
