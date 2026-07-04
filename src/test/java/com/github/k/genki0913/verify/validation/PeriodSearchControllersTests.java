package com.github.k.genki0913.verify.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import com.github.k.genki0913.verify.support.WebLayerTest;
import com.github.k.genki0913.verify.validation.controller.PeriodSearchController;

@WebLayerTest(PeriodSearchController.class)
public class PeriodSearchControllersTests {

        @Autowired
        private MockMvc mockMvc;

        @Nested
        @DisplayName("期間検索バリデーション：初期表示")
        class search {
                @Test
                @DisplayName("GET相関チェック初期表示：正常系（リクエストパラメータがない場合、エラーなしで検索画面が返却されること")
                void givenInitialAccess_whenSearch_thenStatus200AndReturnPeriodSearchViewWithoutErrors()
                                throws Exception {
                        mockMvc.perform(get("/validation/period/search"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/period-search-form"))
                                        .andExpect(model().attributeExists("periodSearchForm"))
                                        .andExpect(model().attribute("successMessage", "日付形式チェック、期間チェックが正常終了しました。"))
                                        .andExpect(model().hasNoErrors());
                }

                @Test
                @DisplayName("GET相関チェック検証：完全正常系（開始日 < 終了日の場合、エラーなしで成功メッセージが設定されること）")
                void givenValidPeriod_whenSearch_thenNoErrorsAndHasSuccessMessage() throws Exception {
                        mockMvc.perform(
                                        get("/validation/period/search")
                                                        .param("startDate", "2026-06-01")
                                                        .param("endDate", "2026-06-02"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/period-search-form"))
                                        .andExpect(model().hasNoErrors())
                                        .andExpect(model().attribute("successMessage", "日付形式チェック、期間チェックが正常終了しました。"));
                }

                @Test
                @DisplayName("GET相関チェック検証：境界値正常系（開始日 = 終了日の場合、エラーなしで成功メッセージが設定されること")
                void givenSamePeriod_whenSearch_thenNoErrorsAndHasSuccessMessage() throws Exception {
                        mockMvc.perform(get("/validation/period/search")
                                        .param("startDate", "2026-06-01")
                                        .param("endDate", "2026-06-01"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/period-search-form"))
                                        .andExpect(model().hasNoErrors());
                }

                @Test
                @DisplayName("GET検証：形式エラー異常系（開始日、終了日が日付け意識ではない場合、typeMismatchエラーが発生すること")
                void givenInvalidFormat_whenSearch_thenHasTypeMismatchErrorAndReturnFormView() throws Exception {
                        mockMvc.perform(get("/validation/period/search")
                                        .param("startDate", "テスト")
                                        .param("endDate", "test"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/period-search-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("periodSearchForm", 2))
                                        .andExpect(model().attributeHasFieldErrorCode("periodSearchForm", "startDate",
                                                        "typeMismatch"))
                                        .andExpect(model().attributeHasFieldErrorCode("periodSearchForm", "endDate",
                                                        "typeMismatch"));
                }

                @Test
                @DisplayName("GET検証：相関エラー異常系（開始日 > 終了日の場合、ChronologicalPeriodグローバルエラーが発生すること")
                void givenReversePeriod_whenSearch_thenHasChronologicalPeriodErrorAndReturnFormView() throws Exception {
                        MvcResult result = mockMvc
                                        .perform(get("/validation/period/search")
                                                        .param("startDate", "2026-01-01")
                                                        .param("endDate", "2025-12-31"))
                                        .andExpect(status().isOk())
                                        .andExpect(view().name("validation/period-search-form"))
                                        .andExpect(model().hasErrors())
                                        .andExpect(model().attributeErrorCount("periodSearchForm", 1))
                                        .andReturn();

                        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
                                        .get("org.springframework.validation.BindingResult.periodSearchForm");
                        assertNotNull(bindingResult);
                        assertTrue(bindingResult.hasGlobalErrors(), "グローバルエラーが発生していません");

                        boolean containsSpecificError = bindingResult.getGlobalErrors().stream()
                                        .filter(Objects::nonNull)
                                        .map(error -> error.getDefaultMessage())
                                        .allMatch(message -> Objects.equals(message, "日付の前後関係が不正です"));
                        assertTrue(containsSpecificError, "期待する相関チェックのエラーメッセージがありません");
                }
        }
}
