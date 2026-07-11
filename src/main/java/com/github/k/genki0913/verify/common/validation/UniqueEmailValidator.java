package com.github.k.genki0913.verify.common.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.service.UserRegistrationService;

@Component
public class UniqueEmailValidator implements Validator {

    private final UserRegistrationService userRegistrationService;

    public UniqueEmailValidator(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistForm form = (UserRegistForm) target;
        if (userRegistrationService.isEmailRegistered(form.getEmail())) {
            errors.rejectValue("email", "common.email.duplicate", "入力されたメールアドレスは既に登録されています。");
        }
    }
}
