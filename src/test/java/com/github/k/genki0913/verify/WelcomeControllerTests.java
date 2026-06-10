package com.github.k.genki0913.verify;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WelcomeController.class)
public class WelcomeControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("GET / (ホーム画面にアクセス)")
    class Init {

        @Test
        @DisplayName("リクエストパラメータがない場合、ステータス200、View名がWelcome、HTML内にWelComeが含まれたレスポンスを返すこと")
        void init_nonParam_return200AndWelComeView() throws Exception {
            mockMvc.perform(
                    get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("welcome"))
                    .andExpect(content().string(containsString("Spring Boot 技術検証サンドボックスへようこそ！")));
        }

        @Test
        @DisplayName("リクエストパラメータがある場合、ステータス200、View名がWelcome、HTML内にWelComeが含まれたレスポンスを返すこと")
        void init_existParam_return200AndWelComeVIew() throws Exception {
            mockMvc.perform(
                    get("/")
                            .param("test_key", "test_value"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("welcome"))
                    .andExpect(content().string(containsString("Spring Boot 技術検証サンドボックスへようこそ！")));
            ;
        }
    }
}
