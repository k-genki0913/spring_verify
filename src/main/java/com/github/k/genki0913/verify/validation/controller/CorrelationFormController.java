package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.k.genki0913.verify.validation.form.CorrelationUserRegistForm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/validation/correlation")
public class CorrelationFormController {

    @GetMapping("/form")
    public String showCorrelationForm(CorrelationUserRegistForm correlationUserRegistForm) {
        return "validation/correlation-form";
    }

    @PostMapping("/form")
    public String registUser(@Validated @ModelAttribute("correlationUserRegistForm") CorrelationUserRegistForm form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "validation/correlation-form";
        }

        return "redirect:/validation/correlation/form";
    }

}
