package com.github.k.genki0913.verify.common.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@Component
public class UniqueEmailValidator implements Validator {

    private final UserRepository userRepository;

    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistForm form = (UserRegistForm) target;
        if (userRepository.existsByEmail(form.getEmail())) {
            errors.rejectValue("email", "common.email.duplicate", "入力されたメールアドレスは既に登録されています。");
        }
    }
}
