package com.github.k.genki0913.verify.repository.form;

import com.github.k.genki0913.verify.domain.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public UserUpdateForm() {
    }

    public UserUpdateForm(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
