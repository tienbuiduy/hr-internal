package com.hr.model;

import java.util.Date;

public class AllocationByDay {
    private Integer columnId;
    private Date alloDate;
    private float allo;

    public AllocationByDay() {
    }

    public AllocationByDay(Integer columnId, Date alloDate) {
        this.columnId = columnId;
        this.alloDate = alloDate;
    }

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public Date getAlloDate() {
        return alloDate;
    }

    public void setAlloDate(Date alloDate) {
        this.alloDate = alloDate;
    }

    public float getAllo() {
        return allo;
    }

    public void setAllo(float allo) {
        this.allo = allo;
    }
}
