package com.github.k.genki0913.verify.repository.validation;

import com.github.k.genki0913.verify.repository.form.UserUpdateForm;
import com.github.k.genki0913.verify.repository.service.UserUpdateService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailExceptSelfValidator implements ConstraintValidator<UniqueEmailExceptSelf, UserUpdateForm> {

    private final UserUpdateService userUpdateService;

    public UniqueEmailExceptSelfValidator(UserUpdateService userUpdateService) {
        this.userUpdateService = userUpdateService;
    }

    @Override
    public boolean isValid(UserUpdateForm form, ConstraintValidatorContext context) {

        if (form.getId() == null || form.getEmail() == null) {
            return true;
        }

        boolean isDuplicatedExceptSelf = userUpdateService.isEmailRegisteredByOther(form.getId(), form.getEmail());
        if (isDuplicatedExceptSelf) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{common.email.duplicate.except.self}")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
