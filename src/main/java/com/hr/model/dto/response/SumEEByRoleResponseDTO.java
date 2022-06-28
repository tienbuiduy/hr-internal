package com.hr.model.dto.response;

public class SumEEByRoleResponseDTO {
    private String roleName;
    private float mdAllocateEE;
    private float mdRateEE;
    private float mmAllocateEE;
    private float mmRateEE;

    public SumEEByRoleResponseDTO() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public float getMdAllocateEE() {
        return mdAllocateEE;
    }

    public void setMdAllocateEE(float mdAllocateEE) {
        this.mdAllocateEE = mdAllocateEE;
    }

    public float getMdRateEE() {
        return mdRateEE;
    }

    public void setMdRateEE(float mdRateEE) {
        this.mdRateEE = mdRateEE;
    }

    public float getMmAllocateEE() {
        return mmAllocateEE;
    }

    public void setMmAllocateEE(float mmAllocateEE) {
        this.mmAllocateEE = mmAllocateEE;
    }

    public float getMmRateEE() {
        return mmRateEE;
    }

    public void setMmRateEE(float mmRateEE) {
        this.mmRateEE = mmRateEE;
    }
}
