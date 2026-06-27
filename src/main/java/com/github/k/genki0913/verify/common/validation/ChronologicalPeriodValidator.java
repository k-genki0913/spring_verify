package com.github.k.genki0913.verify.common.validation;

import java.time.LocalDate;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChronologicalPeriodValidator implements ConstraintValidator<ChronologicalPeriod, Object> {

    private String startDateTargetField;
    private String endDateTargetField;

    @Override
    public void initialize(ChronologicalPeriod constraintAnnotation) {
        this.startDateTargetField = constraintAnnotation.startDate();
        this.endDateTargetField = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDate startDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(startDateTargetField);
        LocalDate endDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(endDateTargetField);

        if (startDate == null || endDate == null) {
            return true;
        }

        return startDate.isBefore(endDate) || startDate.equals(endDate);
    }
}
