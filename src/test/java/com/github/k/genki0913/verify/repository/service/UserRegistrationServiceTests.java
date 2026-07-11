package com.github.k.genki0913.verify.repository.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@DataJpaTest
@Import(UserRegistrationService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRegistrationServiceTests {

    @Autowired
    private UserRegistrationService service;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("usersテーブル 新規登録")
    class register {
        @Test
        @DisplayName("ユーザー登録: 正常系")
        void givenNonExistingIdUser_whenRegister_thenSuccessReturnRegisteredUser() {
            String inputEmail = generateUniqueEmail();
            UserRegistForm form = new UserRegistForm();
            form.setName("テスト太郎");
            form.setEmail(inputEmail);
            User user = new User(form);

            User registeredUser = service.register(user);

            assertThat(registeredUser.getId()).isNotNull();
            assertThat(registeredUser.getName()).isEqualTo("テスト太郎");
            assertThat(registeredUser.getEmail()).isEqualTo(inputEmail);
            assertThat(userRepository.findById(registeredUser.getId())).isPresent();
        }

        @Test
        @DisplayName("usersテーブル 新規登録: 引数のインスタンスにIDが含まれる場合に例外をスローする")
        void givenExistingIdUser_whenRegister_thenThrowsIllegalArgumentException() {
            User user = new User();
            user.setId(1L);
            user.setName("テスト太郎");
            user.setEmail(generateUniqueEmail());

            assertThatThrownBy(() -> service.register(user)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    private String generateUniqueEmail() {
        return "test-" + System.nanoTime() + "@example.com";
    }
}
