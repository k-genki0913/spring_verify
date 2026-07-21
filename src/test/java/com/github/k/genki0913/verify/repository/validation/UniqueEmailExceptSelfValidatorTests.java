package com.github.k.genki0913.verify.repository.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.k.genki0913.verify.repository.form.UserUpdateForm;
import com.github.k.genki0913.verify.repository.service.UserUpdateService;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

public class UniqueEmailExceptSelfValidatorTests {

    @Mock
    private UserUpdateService userUpdateService;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;

    private UniqueEmailExceptSelfValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new UniqueEmailExceptSelfValidator(userUpdateService);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString()))
                .thenReturn(mock(
                        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
    }

    @Test
    void givenIdIsNull_whenIsValid_thenReturnTrue() {
        UserUpdateForm form = new UserUpdateForm();
        form.setId(null);
        form.setEmail("test@example.com");

        boolean result = validator.isValid(form, constraintValidatorContext);

        assertThat(result).isTrue();
        verify(userUpdateService, never()).isEmailRegisteredByOther(any(), any());
    }

    @Test
    void givenEmailIsNull_whenIsValid_thenReturnTrue() {
        UserUpdateForm form = new UserUpdateForm();
        form.setId(1L);
        form.setEmail(null);

        boolean result = validator.isValid(form, constraintValidatorContext);

        assertThat(result).isTrue();
        verify(userUpdateService, never()).isEmailRegisteredByOther(any(), any());
    }

    @Test
    void givenNonExistEmailExceptSelf_whenIsValid_thenReturnTrue() {
        UserUpdateForm form = new UserUpdateForm();
        form.setId(1L);
        form.setEmail("unique@example.com");

        when(userUpdateService.isEmailRegisteredByOther(1L, "unique@example.com")).thenReturn(false);

        boolean result = validator.isValid(form, constraintValidatorContext);

        assertThat(result).isTrue();
    }

    @Test
    void givenExistEmailExceptSelf_whenIsValid_thenReturnFalseWithError() {
        UserUpdateForm form = new UserUpdateForm();
        form.setId(1L);
        form.setEmail("duplicate@example.com");

        when(userUpdateService.isEmailRegisteredByOther(1L, "duplicate@example.com")).thenReturn(true);

        boolean result = validator.isValid(form, constraintValidatorContext);

        assertThat(result).isFalse();

        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{common.email.duplicate.except.self}");
        verify(constraintViolationBuilder).addPropertyNode("email");
    }
}
