package com.github.k.genki0913.verify.validation;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class ValidationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Nested
        @DisplayName("単項目バリデーション GETメソッド")
        class executeQuerySingleValidation {
                @Test
                @DisplayName("GET検証：異常系（必須項目のみエラーの場合、必須エラーのみが画面に返却されること）")
                void givenRequiredParamInvalidAndLengthParamValid_whenExecuteQuerySingleValidation_thenStatus200AndReturnSingleViewWithRequiredErrorOnly()
                                throws Exception {
                        mockMvc.perform(get("/validation/single/query")
                                        .param("requiredParam", "")
                                        .param("lengthParam", "12345"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single"))
                                        .andExpect(model().attributeExists("errors"))
                                        .andExpect(content().string(containsString("[必須チェック項目] は必須入力です。")))
                                        .andExpect(content()
                                                        .string(org.hamcrest.Matchers.not(containsString(
                                                                        "[長さチェック項目] は 0 文字以上 5 文字以内で入力してください。"))));
                }

                @Test
                @DisplayName("GET検証：異常系（長さ項目のみエラーの場合、長さエラーのみが画面に返却されること）")
                void givenRequiredParamValidAndLengthParamInvalid_whenExecuteQuerySingleValidation_thenStatus200AndReturnSingleViewWithLengthErrorOnly()
                                throws Exception {
                        mockMvc.perform(get("/validation/single/query")
                                        .param("requiredParam", "あいうえお")
                                        .param("lengthParam", "123456"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/single"))
                                        .andExpect(model().attributeExists("errors"))
                                        .andExpect(content().string(org.hamcrest.Matchers
                                                        .not(containsString("[必須チェック項目] は必須入力です。"))))
                                        .andExpect(content().string(
                                                        containsString("[長さチェック項目] は 0 文字以上 5 文字以内で入力してください。")));
                }

                @Test
                @DisplayName("GET検証：異常系（バリデーションエラーが発生し、例外ハンドラーによってエラーメッセージが画面に返却されること）")
                void givenInvalidParameters_whenExecuteQuerySingleValidation_thenStatus200AndReturnSingleViewWithErrors()
                                throws Exception {
                        mockMvc.perform(get("/validation/single/query").param("requiredParam", "").param("lengthParam",
                                        "123456"))
                                        .andExpect(status().isOk()).andExpect(view().name("validation/single"))
                                        .andExpect(model().attributeExists("errors"))
                                        .andExpect(content().string(containsString("[必須チェック項目] は必須入力です。")))
                                        .andExpect(content().string(
                                                        containsString("[長さチェック項目] は 0 文字以上 5 文字以内で入力してください。")));
                }

                @Test
                @DisplayName("GET検証：正常系（パラメータが適切に入力されている場合、エラーなしで画面が表示されること）")
                void givenValidParameters_whenExecuteQuerySingleValidation_thenStatus200AndReturnSingleViewWithoutErrors()
                                throws Exception {
                        mockMvc.perform(
                                        get("/validation/single/query").param("requiredParam", "あいうえお")
                                                        .param("lengthParam", "12345"))
                                        .andExpect(status().isOk()).andExpect(view().name("validation/single"))
                                        .andExpect(model().attributeDoesNotExist("errors"));
                }
        }
}
