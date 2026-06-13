package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.k.genki0913.verify.validation.form.UserForm;

@Controller
@RequestMapping("/validation")
public class SingleFormController {

    @GetMapping("/single/form")
    public String showSingleForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "validation/single-form";
    }

    @PostMapping("/single/form")
    public String executeFormSingleValidation(@Validated @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "validation/single-form";
        }

        model.addAttribute("successMessagePost", "サーバー側でFormオブジェクトを正常に受け取りました!(POST)");
        model.addAttribute("savedUsername", userForm.getUsername());
        model.addAttribute("savedPassword", userForm.getPassword());

        return "validation/single-form";
    }
}
