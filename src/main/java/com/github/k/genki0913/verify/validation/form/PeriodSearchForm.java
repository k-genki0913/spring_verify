package com.github.k.genki0913.verify.validation.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.github.k.genki0913.verify.common.validation.ChronologicalPeriod;

@ChronologicalPeriod(startDate = "startDate", endDate = "endDate")
public record PeriodSearchForm(
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
}
