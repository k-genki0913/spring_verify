package com.github.k.genki0913.verify.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final RequestCache requestCache = new HttpSessionRequestCache();

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@Validated(LoginForm.ValidationSequence.class) LoginForm loginForm, BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response, Model model) {

        if (bindingResult.hasErrors()) {
            return "login/login";
        }

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginForm.getEmail(),
                    loginForm.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                String originalRequestUrl = savedRequest.getRedirectUrl();
                requestCache.removeRequest(request, response);

                return "redirect:" + originalRequestUrl;
            }

            return "redirect:/";
        } catch (AuthenticationException e) {
            model.addAttribute("loginError", "メールアドレスまたはパスワードが間違っています。");
            return "login/login";
        }
    }

}
