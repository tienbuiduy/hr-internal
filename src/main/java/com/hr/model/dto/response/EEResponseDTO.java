package com.hr.model.dto.response;

public class EEResponseDTO {
    private String roleName;
    private String employeeName;
    private float rate;
    private float mdAllocateEE;
    private float mdRateEE;
    private float mmAllocateEE;
    private float mmRateEE;

    public EEResponseDTO() {
    }

    public EEResponseDTO(String roleName, String employeeName, float rate, float mdAllocateEE, float mdRateEE, float mmAllocateEE, float mmRateEE) {
        this.roleName = roleName;
        this.employeeName = employeeName;
        this.rate = rate;
        this.mdAllocateEE = mdAllocateEE;
        this.mdRateEE = mdRateEE;
        this.mmAllocateEE = mmAllocateEE;
        this.mmRateEE = mmRateEE;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
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
