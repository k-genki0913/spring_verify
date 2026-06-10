package com.github.k.genki0913.verify;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String init() {
        return "welcome";
    }
}
