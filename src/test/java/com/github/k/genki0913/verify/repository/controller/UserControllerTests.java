package com.github.k.genki0913.verify.repository.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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

import com.github.k.genki0913.verify.repository.constant.View;
import com.github.k.genki0913.verify.repository.form.UserRegistForm;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("users-test-data.sql")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("一覧表示の結合テスト")
    class view {

        @Test
        @DisplayName("検索キーワードなし、全データを取得できること")
        void givenUsersExist_whenGetUsesWithoutKeyword_thenReturnsAllUsers() throws Exception {
            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name(View.VIEW))
                    .andExpect(model().attribute("users", hasSize(3)))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(1L)),
                                    hasProperty("name", is("山田太郎")),
                                    hasProperty("email", is("taro@example.com"))))))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(2L)),
                                    hasProperty("name", is("山田花子")),
                                    hasProperty("email",
                                            is("hanako@example.com"))))))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(3L)),
                                    hasProperty("name", is("佐藤次郎")),
                                    hasProperty("email",
                                            is("jiro@example.com"))))));
        }

        @Test
        @DisplayName("検索キーワードで絞り込み、対象の単一データを取得できること")
        void givenUsersExist_whenGetUsersWithKeyword_thenReturnsFilteredUser() throws Exception {
            mockMvc.perform(get("/users")
                    .param("keyword", "佐藤"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name(View.VIEW))
                    .andExpect(model().attribute("users", hasSize(1)))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(3L)),
                                    hasProperty("name", is("佐藤次郎")),
                                    hasProperty("email",
                                            is("jiro@example.com"))))));
        }

        @Test
        @DisplayName("検索キーワードで絞り込み、対象の複数データを取得できること")
        void givenUsersExist_whenGetUsersWithKeyword_thenReturnsFilteredUsers() throws Exception {
            mockMvc.perform(get("/users")
                    .param("keyword", "山田"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name(View.VIEW))
                    .andExpect(model().attribute("users", hasSize(2)))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(1L)),
                                    hasProperty("name", is("山田太郎")),
                                    hasProperty("email", is("taro@example.com"))))))
                    .andExpect(model().attribute("users", hasItem(
                            allOf(
                                    hasProperty("id", is(2L)),
                                    hasProperty("name", is("山田花子")),
                                    hasProperty("email",
                                            is("hanako@example.com"))))));
        }

        @Test
        @DisplayName("検索キーワードで絞り込み、対象データが存在しないこと")
        void givenUsersExist_whenGetUsersWithKeyword_thenReturnsNoUser() throws Exception {
            mockMvc.perform(get("/users")
                    .param("keyword", "田中"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name(View.VIEW))
                    .andExpect(model().attribute("users", hasSize(0)));
        }

    }

    @Nested
    @DisplayName("ユーザー登録画面の表示")
    class showRegistForm {
        @Test
        @DisplayName("登録画面を表示")
        void givenInitialAccess_whenShowRegistForm_thenStatus200AndReturnUserRegistViewWithUserRegistForm()
                throws Exception {
            mockMvc.perform(get("/users/regist"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.REGIST))
                    .andExpect(model().attributeExists("userRegistForm"))
                    .andExpect(model().attribute("userRegistForm", instanceOf(UserRegistForm.class)));
        }
    }
}
