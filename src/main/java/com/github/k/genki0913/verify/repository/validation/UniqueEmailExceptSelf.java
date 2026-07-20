package com.github.k.genki0913.verify.repository.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailExceptSelfValidator.class)
@Documented
public @interface UniqueEmailExceptSelf {

    String message() default "{com.github.k.genki0913.verify.common.email.duplicate.except.self}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        UniqueEmailExceptSelf[] value();
    }
}
