package com.github.k.genki0913.verify.validation.form;

import com.github.k.genki0913.verify.common.validation.FieldsMatch;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@FieldsMatch(field = "email", fieldToMatch = "retryEmail", message = "メールアドレスと再入力が一致しません")
public class CorrelationUserRegistForm {

    @NotBlank
    private String userId;

    @NotBlank
    @Size(min = 8, max = 16)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9-]+$")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
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
