package com.github.k.genki0913.verify.validation.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.github.k.genki0913.verify.common.validation.ChronologicalPeriod;

@ChronologicalPeriod(startDate = "startDate", endDate = "endDate")
public class PeriodSearchForm {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
