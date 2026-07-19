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
}
