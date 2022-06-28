package com.hr.model;

import java.util.Date;

public class AllocationByPeriod {
    private Integer columnId;
    private Date alloStartDate;
    private Date alloEndDate;
    private float allo;

    public AllocationByPeriod() {
    }

    public AllocationByPeriod(Integer columnId, Date alloStartDate, Date alloEndDate, float allo) {
        this.columnId = columnId;
        this.alloStartDate = alloStartDate;
        this.alloEndDate = alloEndDate;
        this.allo = allo;
    }

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public Date getAlloStartDate() {
        return alloStartDate;
    }

    public void setAlloStartDate(Date alloStartDate) {
        this.alloStartDate = alloStartDate;
    }

    public Date getAlloEndDate() {
        return alloEndDate;
    }

    public void setAlloEndDate(Date alloEndDate) {
        this.alloEndDate = alloEndDate;
    }

    public float getAllo() {
        return allo;
    }

    public void setAllo(float allo) {
        this.allo = allo;
    }
}
