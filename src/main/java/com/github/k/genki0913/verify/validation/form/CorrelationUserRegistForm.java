package com.github.k.genki0913.verify.validation.form;

import com.github.k.genki0913.verify.common.validation.FieldsMatch;
import com.github.k.genki0913.verify.common.validation.ValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@FieldsMatch(field = "email", fieldToMatch = "retryEmail", message = "{MailAddressNotMatch}", groups = ValidationGroup.Correlation.class)
public record CorrelationUserRegistForm(
        @NotBlank(groups = ValidationGroup.Required.class) String userId,

        @NotBlank(groups = ValidationGroup.Required.class) @Size(min = 8, max = 16, groups = ValidationGroup.Format.class) @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9-]+$", groups = ValidationGroup.Format.class) String password,

        @NotBlank(groups = ValidationGroup.Required.class) @Email(groups = ValidationGroup.Format.class) String email,

        @NotBlank(groups = ValidationGroup.Required.class) String retryEmail) {

    @GroupSequence({
            ValidationGroup.Required.class,
            ValidationGroup.Format.class,
            ValidationGroup.Correlation.class
    })
    public @interface ValidationSequence {
    }
}
