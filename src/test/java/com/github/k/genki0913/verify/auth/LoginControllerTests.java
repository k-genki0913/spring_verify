package com.github.k.genki0913.verify.auth;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("accounts-test-data.sql")
public class LoginControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("ログインページ表示")
    class loginPage {
        @Test
        @DisplayName("初回アクセスした場合、空のloginFormを格納してログインページを返却する")
        void givenFirstAccess_whenLoginPage_thenStatus200AndReturnLoginViewwithEmptyLoginForm() throws Exception {
            mockMvc.perform(get("/login")).andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeExists("loginForm"))
                    .andExpect(model().attribute("loginForm", is(LoginForm.empty())));
        }

        @Test
        @DisplayName("modelにLoginFormがすでに存在する場合、既存のデータが上書きされずにログインページへ遷移する")
        void givenModelWithLoginForm_whenLoginPage_thenStatus200AndReturnLoginViewWithSameLoginForm() throws Exception {
            LoginForm existingForm = new LoginForm("test@example.com", "password1234");

            mockMvc.perform(get("/login")
                    .flashAttr("loginForm", existingForm))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeExists("loginForm"))
                    .andExpect(model().attribute("loginForm", is(existingForm)));
        }

    }

    @Nested
    @DisplayName("ログイン処理")
    class login {
        @Test
        @DisplayName("メールアドレスとパスワードが空の場合、必須チェックのバリデーションエラーになること")
        void givenBlankParams_whenLogin_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/login")
                    .param("email", "")
                    .param("password", "")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeErrorCount("loginForm", 2))
                    .andExpect(model().attributeHasFieldErrorCode("loginForm", "email", "NotBlank"))
                    .andExpect(model().attributeHasFieldErrorCode("loginForm", "password", "NotBlank"));
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な場合、バリデーションエラーとなる")
        void givenInvalidFormatEmail_whenLogin_thenHasEmailErrorAndReturnFormView() throws Exception {
            mockMvc.perform(
                    post("/login")
                            .param("email", "invalid-email-format")
                            .param("password", "validPassword1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeErrorCount("loginForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("loginForm", "email", "Email"));
        }

        @Test
        @DisplayName("メールアドレスが255文字を超える場合、バリデーションエラーとなる")
        void givenTooLongEmail_whenLogin_thenHasSizeAndEmailErrorAndReturnFormView() throws Exception {
            String tooLongEmail = "a".repeat(246).concat("@example.com");

            mockMvc.perform(post("/login")
                    .param("email", tooLongEmail)
                    .param("password", "validPassword1234")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeErrorCount("loginForm", 2))
                    .andExpect(model().attributeHasFieldErrorCode("loginForm", "email", is(oneOf("Size", "Email"))));
        }

        @Test
        @DisplayName("パスワードが8文字未満の場合、バリデーションエラーとなる")
        void givenTooShortPassword_whenLogin_thenHasSizeErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/login")
                    .param("email", "test@example.com")
                    .param("password", "short")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login/login"))
                    .andExpect(model().attributeErrorCount("loginForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("loginForm", "password", "Size"));
        }

        @Test
        @DisplayName("正しい認証情報でログインした場合、ホームページへリダイレクトする")
        void testLogin_Success() throws Exception {
            mockMvc.perform(post("/login")
                    .param("email", "test@example.com")
                    .param("password", "password")
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(request().sessionAttribute(
                            SPRING_SECURITY_CONTEXT_KEY,
                            notNullValue()))
                    .andExpect(request().sessionAttribute(
                            SPRING_SECURITY_CONTEXT_KEY,
                            hasProperty("authentication",
                                    allOf(
                                            hasProperty("authenticated", is(true)),
                                            hasProperty("principal",
                                                    hasProperty("username", is("test@example.com")))))));
        }
    }
}
