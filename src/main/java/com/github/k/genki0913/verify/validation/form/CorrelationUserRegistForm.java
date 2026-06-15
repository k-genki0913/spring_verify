package com.github.k.genki0913.verify.validation.form;

import com.github.k.genki0913.verify.common.validation.FieldsMatch;
import com.github.k.genki0913.verify.common.validation.ValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@GroupSequence({
        ValidationGroup.Required.class,
        ValidationGroup.Format.class,
        ValidationGroup.Correlation.class,
        CorrelationUserRegistForm.class
})
@FieldsMatch(field = "email", fieldToMatch = "retryEmail", message = "{MailAddressNotMatch}", groups = ValidationGroup.Correlation.class)
public class CorrelationUserRegistForm {

    @NotBlank(groups = ValidationGroup.Required.class)
    private String userId;

    @NotBlank(groups = ValidationGroup.Required.class)
    @Size(min = 8, max = 16, groups = ValidationGroup.Format.class)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9-]+$", groups = ValidationGroup.Format.class)
    private String password;

    @NotBlank(groups = ValidationGroup.Required.class)
    @Email(groups = ValidationGroup.Format.class)
    private String email;

    @NotBlank(groups = ValidationGroup.Required.class)
    private String retryEmail;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRetryEmail() {
        return this.retryEmail;
    }

    public void setRetryEmail(String retryEmail) {
        this.retryEmail = retryEmail;
    }
}
