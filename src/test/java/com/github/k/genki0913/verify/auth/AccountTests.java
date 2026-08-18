package com.github.k.genki0913.verify.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Nested
    @DisplayName("一般ユーザー 登録")
    class createUser {
        @Test
        @DisplayName("一般ユーザー登録: 正常系")
        void givenValidParam_whenRegister_thenSuccessWithAccountByROLE_USER() {
            Account account = Account.createUser("test@example.com", "password1234", "テストユーザー");

            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getPassword()).isEqualTo("password1234");
            assertThat(account.getName()).isEqualTo("テストユーザー");
            assertThat(account.getRole()).isEqualTo("ROLE_USER");
            assertThat(account.getEnabled()).isTrue();
            assertThat(account.getFailedAttempts()).isEqualTo(0);
            assertThat(account.getAccountLocked()).isFalse();
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(メールアドレスがnull)")
        void givenNulltoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser(null, "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(メールアドレスが空文字)")
        void givenEmptytoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(メールアドレスが空白)")
        void givenFullWidthCharacterBlanktoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("　", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(メールアドレスが空白)")
        void givenHalfWidthCharacterBlanktoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser(" ", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(パスワードがnull)")
        void givenNulltoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", null, "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(パスワードが空文字)")
        void givenEmptytoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(パスワードが全角空白)")
        void givenFullWidthCharacterBlanktoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "　", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(パスワードが半角空白)")
        void givenHalfWidthCharacterBlanktoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", " ", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(名前がnull)")
        void givenNulltoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "password1234", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(名前が空文字)")
        void givenEmptytoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "password1234", ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(名前が全角空白)")
        void givenFullWidthCharacterBlanktoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "password1234", "　"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("一般ユーザー: 異常系(名前が半角空白)")
        void givenHalfWidthCharacterBlanktoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createUser("test@example.com", "password1234", " "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }
    }

    @Nested
    @DisplayName("管理者ユーザー 登録")
    class createAdmin {
        @Test
        @DisplayName("管理者ユーザー登録: 正常系")
        void givenValidParam_whenRegister_thenSuccessWithAccountByROLE_USER() {
            Account account = Account.createAdmin("test@example.com", "password1234", "テストユーザー");

            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getPassword()).isEqualTo("password1234");
            assertThat(account.getName()).isEqualTo("テストユーザー");
            assertThat(account.getRole()).isEqualTo("ROLE_ADMIN");
            assertThat(account.getEnabled()).isTrue();
            assertThat(account.getFailedAttempts()).isEqualTo(0);
            assertThat(account.getAccountLocked()).isFalse();
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(メールアドレスがnull)")
        void givenNulltoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin(null, "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(メールアドレスが空文字)")
        void givenEmptytoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(メールアドレスが空白)")
        void givenFullWidthCharacterBlanktoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("　", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(メールアドレスが空白)")
        void givenHalfWidthCharacterBlanktoEmailParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin(" ", "password1234", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(パスワードがnull)")
        void givenNulltoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", null, "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(パスワードが空文字)")
        void givenEmptytoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(パスワードが全角空白)")
        void givenFullWidthCharacterBlanktoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "　", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(パスワードが半角空白)")
        void givenHalfWidthCharacterBlanktoPasswordParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", " ", "テストユーザー"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(名前がnull)")
        void givenNulltoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "password1234", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(名前が空文字)")
        void givenEmptytoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "password1234", ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(名前が全角空白)")
        void givenFullWidthCharacterBlanktoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "password1234", "　"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }

        @Test
        @DisplayName("管理者ユーザー: 異常系(名前が半角空白)")
        void givenHalfWidthCharacterBlanktoNameParam_whenRegister_thenThrowIllegalArgumentException() {
            assertThatThrownBy(() -> Account.createAdmin("test@example.com", "password1234", " "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be null or blank");
        }
    }

    @Nested
    @DisplayName("ログイン失敗記録")
    class recordFailedLogin {
        @Test
        @DisplayName("最大回数未満の場合はロックされないこと")
        void givenAttemptLessThanMaxAttempt_whenRecordFailedLogin_thenCountUpAttemptNotLocked() {
            Account account = Account.createUser("test@example.com", "password1234", "テストユーザー");
            final int maxAttempts = 3;

            account.recordFailedLogin(maxAttempts);

            assertThat(account.getFailedAttempts()).isEqualTo(1);
            assertThat(account.getAccountLocked()).isFalse();
            assertThat(account.getLockTime()).isNull();
        }

        @Test
        @DisplayName("最大回数に達した場合はアカウントがロックされること")
        void givenAttemptMaxAttemptOrMore_whenRecordFailedLogin_thenCountUpAttemptLocked() {
            Account account = Account.createUser("test@example.com", "password1234", "テストユーザー");
            final int maxAttempts = 3;

            account.recordFailedLogin(maxAttempts);
            account.recordFailedLogin(maxAttempts);
            account.recordFailedLogin(maxAttempts);

            assertThat(account.getFailedAttempts()).isEqualTo(3);
            assertThat(account.getAccountLocked()).isTrue();
            assertThat(account.getLockTime()).isNotNull();
        }
    }

}
