package com.github.k.genki0913.verify.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.github.k.genki0913.verify.domain.User;

@DataJpaTest
@Sql("users-test-data.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("名前の部分一致検索")
    class findByNameContaining {
        @Test
        @DisplayName("名前の一致するデータが0件の場合")
        void givenNoMatchingName_whenFindByNameContaining_thenEmptyList() {
            List<User> users = userRepository.findByNameContaining("鈴木");
            assertThat(users).isEmpty();
        }

        @Test
        @DisplayName("名前の一致するデータが1件の場合")
        void givenSingleMatchingName_whenFindByNameContaining_thenReturnsSingleUser() {
            List<User> users = userRepository.findByNameContaining("佐藤");

            assertThat(users).hasSize(1);
            assertThat(users.get(0).getName()).isEqualTo("佐藤次郎");
        }

        @Test
        @DisplayName("名前の一致するデータが2件の場合")
        void givenMultipleMatchingNames_whenFindByNameContaining_thenReturnsAllMatchingUsers() {
            List<User> users = userRepository.findByNameContaining("山田");

            assertThat(users).hasSize(2);
            assertThat(users).extracting(user -> user.getName())
                    .containsExactlyInAnyOrder("山田太郎", "山田花子");
        }
    }

    @Nested
    @DisplayName("メールアドレスの存在チェック(完全一致)")
    class existsByEmail {

        @Test
        @DisplayName("メールアドレスが存在する場合")
        void givenExistingEmail_whenExistsByEmail_thenReturnTrue() {
            boolean exists = userRepository.existsByEmail("taro@example.com");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("メールアドレスが存在しない場合")
        void givenNonExistingEmail_whenExistsByEmail_thenReturnFalse() {
            boolean exists = userRepository.existsByEmail("unknown@example.com");
            assertThat(exists).isFalse();
        }
    }
}
