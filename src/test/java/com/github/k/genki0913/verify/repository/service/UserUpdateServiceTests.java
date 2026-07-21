package com.github.k.genki0913.verify.repository.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.exception.UserNotFoundException;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserUpdateServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUpdateService userUpdateService;

    @Nested
    @DisplayName("更新ユーザーを取得する")
    class findByid {
        @Test
        @DisplayName("ユーザーが存在する場合、Userオブジェクトを返すこと")
        void givenExistUserId_whenFindById_thenReturnUserEntityObject() {
            Long id = 1L;
            User expectedUser = new User();
            expectedUser.setId(id);

            when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

            User actualUser = userUpdateService.findById(id);

            assertThat(actualUser).isEqualTo(expectedUser);
            verify(userRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("ユーザーが存在しない場合、UserNotFoundExceptionがスローされること")
        void givenNotExistUserId_whenFindById_thenThrowUserNotFoundException() {
            Long id = 999L;
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userUpdateService.findById(id))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            verify(userRepository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("更新メールアドレスが別ユーザーで利用されているか確認する")
    class isEmailRegisteredByOther {
        @Test
        @DisplayName("更新メールアドレスを利用しているユーザーがいない場合、falseを返す")
        void givenNotExistEmail_whenIsEmailRegisteredByOther_thenReturnFalse() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            boolean result = userUpdateService.isEmailRegisteredByOther(1L, "test@example.com");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("更新メールアドレスを利用しているユーザーが存在し、別IDの場合、trueを返す")
        void givenExistEmailExceptSelf_whenIsEmailRegisteredByOther_thenReturnTrue() {
            User existingOther = new User();
            existingOther.setId(2L);
            existingOther.setName("テスト別太郎");
            existingOther.setEmail("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingOther));

            boolean result = userUpdateService.isEmailRegisteredByOther(1L, "test@example.com");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("更新メールアドレスを利用しているユーザーが存在し、同一IDの場合、falseを返す")
        void givenExistEmailOfSelf_whenIsEmailRegisteredByOther_thenReturnFalse() {
            User existingSelf = new User();
            existingSelf.setId(1L);
            existingSelf.setName("テスト太郎");
            existingSelf.setEmail("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingSelf));

            boolean result = userUpdateService.isEmailRegisteredByOther(1L, "test@example.com");

            assertThat(result).isFalse();
        }
    }
}
