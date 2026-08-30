package com.github.k.genki0913.verify.repository.form;

import com.github.k.genki0913.verify.common.validation.ValidationGroup;
import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.validation.UniqueEmailExceptSelf;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@UniqueEmailExceptSelf(groups = ValidationGroup.Correlation.class)
@SuppressWarnings("PMD.DataClass")
public class UserUpdateForm {

    @GroupSequence({
            ValidationGroup.Required.class,
            ValidationGroup.Format.class,
            ValidationGroup.Correlation.class
    })
    public @interface ValidationSequence {
    }

    @NotNull(groups = ValidationGroup.Required.class)
    private Long id;

    @NotBlank(groups = ValidationGroup.Required.class)
    private String name;

    @NotBlank(groups = ValidationGroup.Required.class)
    @Email(groups = ValidationGroup.Format.class)
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
