package com.github.k.genki0913.verify.auth;

import com.github.k.genki0913.verify.common.validation.ValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginForm {

    @GroupSequence({
            ValidationGroup.Required.class,
            ValidationGroup.Format.class
    })
    public @interface ValidationSequence {
    }

    @NotBlank(groups = ValidationGroup.Required.class)
    @Email(groups = ValidationGroup.Format.class)
    @Size(max = 255, groups = ValidationGroup.Format.class)
    private String email;

    @NotBlank(groups = ValidationGroup.Required.class)
    @Size(min = 8, max = 255, groups = ValidationGroup.Format.class)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
