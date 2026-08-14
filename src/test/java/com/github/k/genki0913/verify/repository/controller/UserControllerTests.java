package com.github.k.genki0913.verify.repository.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.k.genki0913.verify.repository.constant.View;
import com.github.k.genki0913.verify.repository.form.UserRegistForm;

import jakarta.transaction.Transactional;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
@Transactional
@Sql("users-test-data.sql")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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
                    .andExpect(model().attribute("userRegistForm",
                            instanceOf(UserRegistForm.class)));
        }
    }

    @Nested
    @DisplayName("ユーザー登録処理")
    class regist {
        @Test
        @DisplayName("登録処理: 入力値が不正な場合、エラー画面に戻ること")
        void givenEmptyAllParams_whenRegist_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/regist")
                    .param("name", "")
                    .param("email", "")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.REGIST))
                    .andExpect(model().attributeErrorCount("userRegistForm", 2))
                    .andExpect(model().attributeHasFieldErrorCode("userRegistForm", "name",
                            "NotBlank"))
                    .andExpect(model().attributeHasFieldErrorCode("userRegistForm", "email",
                            "NotBlank"));
        }

        @Test
        @DisplayName("登録処理: メールアドレスにアットマークが含まれていない場合、パターンエラーが発生し、元画面が再描画されること")
        void givenNotExistAtSignEmail_whenRegist_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/regist")
                    .param("name", "テスト太郎")
                    .param("email", "test")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.REGIST))
                    .andExpect(model().attributeErrorCount("userRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userRegistForm", "email",
                            "Email"));
        }

        @Test
        @DisplayName("登録処理: メールアドレスにドメインが含まれていない場合、パターンエラーが発生し、元画面が再描画されること")
        void givenNotExistDomainEmail_whenRegist_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/regist")
                    .param("name", "テスト太郎")
                    .param("email", "test@")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.REGIST))
                    .andExpect(model().attributeErrorCount("userRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userRegistForm", "email",
                            "Email"));
        }

        @Test
        @DisplayName("登録処理: メールアドレスが既に存在する場合、重複エラーが発生し、元画面が再描画されること")
        void givenDuplicatedEmail_whenRegist_thenHasCommonEmailDuplicateErrorAndReturnFormView()
                throws Exception {
            mockMvc.perform(post("/users/regist")
                    .param("name", "テスト太郎")
                    .param("email", "taro@example.com")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.REGIST))
                    .andExpect(model().attributeErrorCount("userRegistForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userRegistForm", "email",
                            "common.email.duplicate"));
        }

        @Test
        @DisplayName("登録処理: 正常終了")
        void givenValidInput_whenRegist_thenNoErrorsAndRedirectForm() throws Exception {
            mockMvc.perform(post("/users/regist")
                    .param("name", "テスト太郎")
                    .param("email", "test@example")
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/users"))
                    .andExpect(model().hasNoErrors());
        }
    }

    @Nested
    @DisplayName("ユーザー編集画面表示")
    class showEditForm {

        @Test
        @DisplayName("更新対象ユーザーが存在する場合")
        void givenExistUserId_whenShowEditForm_thenStatus200AndReturnUserEditViewWithUserUpdateForm()
                throws Exception {
            mockMvc.perform(get("/users/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeExists("userUpdateForm"))
                    .andExpect(model().attribute("userUpdateForm", hasProperty("id", is(1L))))
                    .andExpect(model().attribute("userUpdateForm", hasProperty("name", is("山田太郎"))))
                    .andExpect(model().attribute("userUpdateForm",
                            hasProperty("email", is("taro@example.com"))));
        }

        @Test
        @DisplayName("更新対象ユーザーが存在しない場合")
        void givenNotExistUserId_whenShowEditForm_thenStatus404AndReturn404ErrorView() throws Exception {
            mockMvc.perform(get("/users/999/edit"))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error/404"));
        }
    }

    @Nested
    @DisplayName("ユーザー更新処理")
    class update {
        @Test
        @DisplayName("POST検証: 異常系(id、名前、メールアドレスが未入力の場合、必須バリデーションエラーが発生し、元画面が再描画されること)")
        void givenEmptyAllParams_whenUpdate_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "")
                    .param("name", "")
                    .param("email", "")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 3))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "id",
                            "NotNull"))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "name",
                            "NotBlank"))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "email",
                            "NotBlank"));
        }

        @Test
        @DisplayName("POST検証: 異常系(名前が未入力の場合、必須バリデーションエラーが発生し、元画面が再描画されること)")
        void givenEmptyIdParam_whenUpdate_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "")
                    .param("name", "テスト太郎")
                    .param("email", "test@example.com")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "id",
                            "NotNull"));
        }

        @Test
        @DisplayName("POST検証: 異常系(名前が未入力の場合、必須バリデーションエラーが発生し、元画面が再描画されること)")
        void givenEmptyNameParam_whenUpdate_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "999")
                    .param("name", "")
                    .param("email", "test@example.com")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "name",
                            "NotBlank"));
        }

        @Test
        @DisplayName("POST検証: 異常系(メールアドレスが未入力の場合、必須バリデーションエラーが発生し、元画面が再描画されること)")
        void givenEmptyEmailParam_whenUpdate_thenHasRequiredErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "999")
                    .param("name", "テスト太郎")
                    .param("email", "")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "email",
                            "NotBlank"));
        }

        @Test
        @DisplayName("POST検証: 異常系(メールアドレスにアットマークが含まれていない場合、パターンバリデーションエラーが発生し、元画面が再描画されること)")
        void givenNotExistAtSignEmail_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "999")
                    .param("name", "テスト太郎")
                    .param("email", "test")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "email",
                            "Email"));
        }

        @Test
        @DisplayName("POST検証: 異常系(メールアドレスにドメインが含まれていない場合、パターンバリデーションエラーが発生し、元画面が再描画されること)")
        void givenNotExistDomainEmail_thenHasPatternErrorAndReturnFormView() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "999")
                    .param("name", "テスト太郎")
                    .param("email", "test@")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "email",
                            "Email"));
        }

        @Test
        @DisplayName("POST検証: 異常系(メールアドレスが更新者以外のIDで利用されている場合、パターンバリデーションエラーが発生し、元画面が再描画されること)")
        void givenExistEmailByOther_whenUpdate_thenHasUniqueEmailExceptSelfErrorAndReturnFormView()
                throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "999")
                    .param("name", "テスト太郎")
                    .param("email", "taro@example.com")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(View.EDIT))
                    .andExpect(model().attributeErrorCount("userUpdateForm", 1))
                    .andExpect(model().attributeHasFieldErrorCode("userUpdateForm", "email",
                            "UniqueEmailExceptSelf"));
        }

        @Test
        @DisplayName("POST検証: 正常系(全ての入力値が正常な場合、エラーなしでユーザー一覧画面へリダイレクトされること)")
        void givenValidInput_whenUpdate_thenNoErrorsAndRedirectToUsers() throws Exception {
            mockMvc.perform(post("/users/update")
                    .param("id", "3")
                    .param("name", "テスト太郎")
                    .param("email", "test@example.com")
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/users"))
                    .andExpect(model().hasNoErrors());
        }
    }
}
