package com.hr.service;

import com.hr.model.AllocationDetail;

public interface AllocationDetailService {
    void deleteByAllocationId(Integer allocationId);

    void saveAllocationDetail(AllocationDetail allocationDetail);
}
