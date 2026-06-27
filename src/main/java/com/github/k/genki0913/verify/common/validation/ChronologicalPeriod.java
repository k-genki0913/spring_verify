package com.github.k.genki0913.verify.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChronologicalPeriodValidator.class)
@Documented
public @interface ChronologicalPeriod {
    String message() default "{com.github.k.genki0913.verify.common.validation.ChronologicalPeriod.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startDate();

    String endDate();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ChronologicalPeriod[] value();
    }
}
