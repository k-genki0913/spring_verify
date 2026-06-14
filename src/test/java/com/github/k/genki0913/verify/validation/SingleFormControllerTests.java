package com.github.k.genki0913.verify.validation;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SingleFormControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Nested
        @DisplayName("単項目バリデーション 初期表示(POST検証用画面")
        class showSingleForm {
                @Test
                @DisplayName("POST検証初期表示：正常系(空のFormオブジェクトがModelに設定され、初期表示画面が返却されること")
                void givenInitialAccess_whenShowSingleForm_thenStatus200AndReturnSingleFormViewWithUserForm()
                                throws Exception {
                        mockMvc.perform(get("/validation/single/form"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().attributeExists("userForm"));
                }
        }

        @Nested
        @DisplayName("単項目バリデーション POSTメソッド")
        class executeFormSingleValidation {
                @Test
                @DisplayName("POST検証：正常系（すべてのFormパラメータが適切に入力されている場合、エラーなしで成功メッセージが返却されること）")
                void givenValidFormParameters_whenExecuteFormSingleValidation_thenStatus200AndReturnSingleFormViewWithSuccessMessage()
                                throws Exception {
                        mockMvc.perform(post("/validation/single/form")
                                        .param("username", "springUser")
                                        .param("password", "pass-1234")
                                        .with(csrf()))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().hasNoErrors())
                                        .andExpect(model().attributeExists("successMessagePost"))
                                        .andExpect(content().string(containsString("サーバー側でFormオブジェクトを正常に受け取りました")));
                }

                @Test
                @DisplayName("POST検証：異常系(段階検証1：全項目が空の場合、必須エラーのみが画面に返却され、文字数等の後続エラーが含まれないこと)")
                void givenAllFieldsEmpty_whenExecuteFormSingleValidation_thenStatus200AndReturnSingleFormViewWithRequiredErrorsOnly()
                                throws Exception {
                        mockMvc.perform(post("/validation/single/form")
                                        .param("username", "")
                                        .param("password", "")
                                        .with(csrf()))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("userForm", 2))
                                        .andExpect(model().attributeHasFieldErrorCode("userForm", "username",
                                                        "NotBlank"))
                                        .andExpect(model().attributeHasFieldErrorCode("userForm", "password",
                                                        "NotBlank"));
                }

                @Test
                @DisplayName("POST検証：異常系（ユーザー名のみ入力・パスワード空の場合、passwordフィールドにのみSizeエラーが設定されること）")
                void givenUsernameValidAndPasswordEmpty_whenExecuteFormSingleValidation_thenStatus200AndReturnSingleFormViewWithPasswordErrorOnly()
                                throws Exception {
                        mockMvc.perform(post("/validation/single/form")
                                        .param("username", "springUser")
                                        .param("password", "")
                                        .with(csrf()))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("userForm", 1))
                                        .andExpect(model().attributeHasFieldErrorCode("userForm", "password",
                                                        "NotBlank"));
                }

                @Test
                @DisplayName("POST検証：異常系（ユーザー名空・パスワードのみ入力の場合、usernameフィールドにのみNotBlankエラーが設定されること）")
                void givenUsernameEmptyAndPasswordValid_whenExecuteFormSingleValidation_thenStatus200AndReturnSingleFormViewWithUsernameErrorOnly()
                                throws Exception {
                        mockMvc.perform(post("/validation/single/form")
                                        .param("username", "")
                                        .param("password", "pass-1234")
                                        .with(csrf()))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("userForm", 1))
                                        .andExpect(model().attributeHasFieldErrorCode("userForm", "username",
                                                        "NotBlank"));
                }

                @Test
                @DisplayName("POST検証：異常系（段階検証2：必須は満たすが文字数や形式が不正な場合、既定グループのエラーメッセージのみが画面に返却されること）")
                void givenInvalidFormatParameters_whenExecuteFormSingleValidation_thenStatus200AndReturnSingleFormViewWithFormatErrors()
                                throws Exception {
                        mockMvc.perform(post("/validation/single/form")
                                        .param("username", "abc")
                                        .param("password", "short")
                                        .with(csrf()))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("userForm", 2))
                                        .andExpect(model().attributeHasFieldErrorCode("userForm", "password",
                                                        anyOf(is("Size"), is("Pattern"))));
                }
        }

}
