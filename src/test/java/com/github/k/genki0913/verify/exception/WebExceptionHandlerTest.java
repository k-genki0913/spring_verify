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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@WebMvcTest(controllers = WebExceptionHandlerTest.TestController.class)
@Import(WebExceptionHandler.class)
@DisplayName("グローバルHTML例外ハンドラのテスト")
public class WebExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("NoResourceFoundException(404エラー)のハンドリング")
    class HandleNotFound {
        @Test
        @DisplayName("【正常系】存在しないリソースをリクエストした時、共通のエラー画面(error/404)を返すこと")
        void givenNonExistentResource_whenRequest_thenStatus404AndReturnErrorView() throws Exception {
            mockMvc.perform(get("/test/404"))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error/404"));
        }
    }

    @Nested
    @DisplayName("HttpRequestMethodNotSupportedException (405エラー) のハンドリング")
    class HandleMethodNotAllowed {
        @Test
        @DisplayName("【正常系】許可されていないメソッドでリクエストした時、共通のエラー画面(error/405)とAllowヘッダーを返すこと")
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
     * * ■ なぜメソッド内部でエラーをスローしているのか
     * MockMvcのルーティング判定に依存してエラーを起こそうとすると、
     * Springの内部仕様(Viewの強制描画チェック等)により、ハンドラに届く前に
     * テスト自体が「No ModelAndView found」等でクラッシュするため。
     * * ■ 解決策
     * メソッド内部で「本物の例外」を手動でthrowする。
     * これによりMockMvcの気まぐれな仕様を回避し、例外ハンドラだけを確実にテストできる。
     */
    @Controller
    static class TestController {

        @GetMapping("/test/404")
        public String trigger404() throws NoResourceFoundException {
            // 【重要】MockMvcの仕様を回避するため、内部で新世代の404例外を手動で発生させる
            // 引数1: 叩かれたリクエストメソッド(HttpMethod.GET)
            // 引数2: 見つからなかったパス("/test/404")
            throw new NoResourceFoundException(HttpMethod.GET, "/test/404", "/test/404");
        }

        @GetMapping("/test/405")
        public String trigger405() throws HttpRequestMethodNotSupportedException {
            // 【重要】MockMvcの仕様を回避するため、内部で新世代の404例外を手動で発生させる
            // 引数1: 送られてきたリクエストメソッド(GET)
            // 引数2: 許可されているリクエストメソッド(POST)
            throw new HttpRequestMethodNotSupportedException("GET", List.of("POST"));
        }

    }
}
