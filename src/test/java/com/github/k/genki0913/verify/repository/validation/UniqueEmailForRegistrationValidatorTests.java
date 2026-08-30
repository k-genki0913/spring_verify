package com.github.k.genki0913.verify.repository.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.service.UserRegistrationService;

@ExtendWith(MockitoExtension.class)
public class UniqueEmailForRegistrationValidatorTests {

    @Mock
    private UserRegistrationService userRegistrationService;

    @InjectMocks
    private UniqueEmailForRegistrationValidator uniqueEmailForRegistrationValidator;

    @Test
    @DisplayName("メールアドレスが重複している場合、エラーが登録されること")
    void givenDuplicateEmail_whenValidate_thenReturnEmailFieldError() {
        UserRegistForm form = new UserRegistForm("", "duplicatedEmail@example.com");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(form, "userRegistForm");

        doReturn(true).when(userRegistrationService).isEmailRegistered("duplicatedEmail@example.com");

        uniqueEmailForRegistrationValidator.validate(form, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("email").getCode()).isEqualTo("common.email.duplicate");
        verify(userRegistrationService, times(1)).isEmailRegistered("duplicatedEmail@example.com");
    }

    @Test
    @DisplayName("メールアドレスが重複していない場合、エラーが登録さないこと")
    void givenUniqueEmail_whenValidate_thenReturnNonError() {
        UserRegistForm form = new UserRegistForm("", "uniqueEmail@example.com");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(form, "userRegistForm");

        doReturn(false).when(userRegistrationService).isEmailRegistered("uniqueEmail@example.com");

        uniqueEmailForRegistrationValidator.validate(form, errors);

        assertThat(errors.hasErrors()).isFalse();
        verify(userRegistrationService, times(1)).isEmailRegistered("uniqueEmail@example.com");
    }
}
