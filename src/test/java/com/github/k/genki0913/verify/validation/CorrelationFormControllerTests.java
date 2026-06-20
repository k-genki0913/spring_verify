package com.github.k.genki0913.verify.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.github.k.genki0913.verify.support.WebLayerTest;
import com.github.k.genki0913.verify.validation.controller.CorrelationFormController;

@WebLayerTest(CorrelationFormController.class)
public class CorrelationFormControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("相関バリデーション 初期表示(POST検証")
    class showCorrelationForm {
        @Test
        @DisplayName("POST検証初期表示：正常系(からのFormオブジェクトがModelに設定され、初期表示画面が返却されること")
        void givenInitialAccess_whenShowCorrelationForm_thenStatus200AndReturnCorrelationFormViewWithCorrelationUserRegistForm()
                throws Exception {
            mockMvc.perform(get("/validation/correlation/form"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form"))
                    .andExpect(model().attributeExists("correlationUserRegistForm"));
        }
    }

    @Nested
    @DisplayName("相関バリデーション：正常系 バリデーション実行(POST)送信")
    class registUser {
        @Test
        @DisplayName("POST検証：完全正常系(全ての入力値が正常な場合、エラーなしで初期表示へリダイレクトされること")
        void givenValidInput_whenRegistUser_thenNoErrorsAndRedirecttoForm() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "Pass1234-")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/validation/correlation/form"))
                    .andExpect(model().hasNoErrors());
        }

        @Test
        @DisplayName("POST検証：異常系(ユーザーID、パスワード、メールアドレス、再入力が未入力の場合、必須バリデーションエラーが発生し、元画面が再描画されること)")
        void givenEmptyAllParams_whenRegistUser_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "")
                    .param("password", "")
                    .param("email", "")
                    .param("retryEmail", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 4))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "userId", "NotBlank"))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "NotBlank"))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "email", "NotBlank"))
                    .andExpect(
                            model().attributeHasFieldErrorCode("correlationUserRegistForm", "retryEmail", "NotBlank"));
        }

        @Test
        @DisplayName("POST検証：異常系(パスワードが7文字以下の場合、サイズエラーが発生し、元画面が再描画されること)")
        void givenTooShortPassword_whenRegistUser_thenHasSizeErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "Pass123")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "Size"));
        }

        @Test
        @DisplayName("POST検証：異常系(パスワードが17文字異常の場合、サイズエラーが発生し、元画面が再描画されること)")
        void givenTooMuchPassword_whenRegistUser_thenHasSizeErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "Password123456789")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "Size"));
        }

        @Test
        @DisplayName("POST検証：異常系(パスワードに英字が含まれていない場合、パターンエラーが発生し、元画面が再描画されること)")
        void givenNotExistAlphabetPassword_whenRegistUser_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "12345678")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "Pattern"));
        }

        @Test
        @DisplayName("POST検証：異常系(パスワードに数字が含まれていない場合、パターンエラーが発生し、元画面が再描画されること)")
        void givenNotExistNumberPassword_whenRegistUser_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "password")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "Pattern"));
        }

        @Test
        @DisplayName("POST検証：異常系(パスワードに許可していない記号が含まれている場合、パターンエラーが発生し、元画面が再描画されること)")
        void givenNotAllowSymbolPassword_whenRegistUser_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "pass1234!")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "password", "Pattern"));
        }

        @Test
        @DisplayName("POST検証：異常系(メールアドレスにアットマークが含まれていない場合、パターンエラーが発生し、元画面が再描画されること)")
        void givenNotExistAtSignEmail_whenRegistUser_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "pass1234")
                    .param("email", "test")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "email", "Email"));
        }

        @Test
        @DisplayName("POST検証：異常系(メールアドレスにドメインが含まれていない場合、パターンエラーが発生し、元画面が再描画されること)")
        void givenNotExistDomainEmail_whenRegistUser_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "pass1234")
                    .param("email", "test@")
                    .param("retryEmail", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form")).andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("correlationUserRegistForm", "email", "Email"));
        }

        @Test
        @DisplayName("POST検証：異常系(メールアドレスが一致しない場合、フィールドマッチエラーが発生し、元画面が再描画されること)")
        void givenDifferentEmail_whenRegistUser_thenHasFieldsMatchErrorAndReturnFormView() throws Exception {
            MvcResult result = mockMvc.perform(post("/validation/correlation/form")
                    .param("userId", "user01")
                    .param("password", "Pass1234-")
                    .param("email", "test@example.com")
                    .param("retryEmail", "test1@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("validation/correlation-form"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeErrorCount("correlationUserRegistForm", 1))
                    .andReturn();

            BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
                    .get("org.springframework.validation.BindingResult.correlationUserRegistForm");
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasGlobalErrors(), "グローバルエラーが発生していません");

            boolean containsSpecificError = bindingResult.getGlobalErrors().stream().map(ObjectError::getDefaultMessage)
                    .allMatch(message -> message.equals("メールアドレスと再入力が一致しません"));
            assertTrue(containsSpecificError, "期待するグローバルエラーメッセージがありません");
        }
    }
}
