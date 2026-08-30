package com.github.k.genki0913.verify.repository.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistForm(
        @NotBlank String name,

        @NotBlank @Email String email) {

    public static UserRegistForm empty() {
        return new UserRegistForm("", "");
    }
}
