package com.github.k.genki0913.verify.repository.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.service.UserRegistrationService;

@Component
public class UniqueEmailForRegistrationValidator implements Validator {

    private final UserRegistrationService userRegistrationService;

    public UniqueEmailForRegistrationValidator(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!supports(target.getClass())) {
            return;
        }
        UserRegistForm form = (UserRegistForm) target;
        if (userRegistrationService.isEmailRegistered(form.email())) {
            errors.rejectValue("email", "common.email.duplicate", "入力されたメールアドレスは既に登録されています。");
        }
    }
}
