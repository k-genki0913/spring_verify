package com.github.k.genki0913.verify.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;

@WebMvcTest(controllers = WebExceptionHandlerTest.TestController.class)
@Import(WebExceptionHandler.class)
@DisplayName("グローバルHTML例外ハンドラのテスト")
public class WebExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("HttpRequestMethodNotSupportedException (405エラー) のハンドリング")
    class HandleMethodNotSupported {
        @Test
        @DisplayName("【405エラー】許可されていないメソッドでリクエストした時、共通のエラー画面(error/405)とAllowヘッダーを返すこと")
        void givenEndpointSupportsOnlyPost_whenRequestWithGet_thenStatus405AndReturnErrorViewAndAllowHeader()
                throws Exception {
            mockMvc.perform(get("/test/405"))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(header().string("Allow", "POST"))
                    .andExpect(view().name("error/405"))
                    .andExpect(model().attribute("requestedMethod", "GET"))
                    .andExpect(model().attributeExists("supportedMethods"));
        }
    }

    // =========================================================================
    // テスト専用のダミーController
    // =========================================================================
    /**
     * 【重要】WebExceptionHandlerの単体テスト専用クラス。
     * * ■ なぜ通常の @PostMapping にしないのか？
     * MockMvcのルーティング判定に依存して405を起こそうとすると、
     * Springの内部仕様（Viewの強制描画チェック等）により、ハンドラに届く前に
     * テスト自体が「No ModelAndView found」等でクラッシュするため。
     * * ■ 解決策
     * あえてGETでアクセスさせ、メソッド内部で「本物の405例外」を手動でthrowする。
     * これによりMockMvcの気まぐれな仕様を回避し、例外ハンドラだけを確実にテストできる。
     */
    @Controller
    static class TestController {

        @GetMapping("/test/405")
        public String samplePost() throws HttpRequestMethodNotSupportedException {
            // GETリクエストに対して、本来は「POST」が許可されている状態の405例外を意図的に発生させる
            throw new HttpRequestMethodNotSupportedException("GET", List.of("POST"));
        }

    }
}
