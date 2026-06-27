package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.k.genki0913.verify.validation.constant.View;
import com.github.k.genki0913.verify.validation.form.PeriodSearchForm;

@Controller
@RequestMapping("/validation/period")
public class PeriodSearchController {

    @GetMapping("/search")
    public String search(@Validated @ModelAttribute("periodSearchForm") PeriodSearchForm form,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return View.PERIOD_SEARCH_FORM;
        }

        model.addAttribute("successMessage", "日付形式チェック、期間チェックが正常終了しました。");

        return View.PERIOD_SEARCH_FORM;
    }
}
