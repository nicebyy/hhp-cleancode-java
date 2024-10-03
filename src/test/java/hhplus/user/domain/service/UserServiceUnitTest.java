package hhplus.user.domain.service;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("존재하는 사용자 ID로 사용자를 성공적으로 조회한다.")
    void findUserById_UserExists() {
        // given
        Long userId = 1L;
        String userName = "홍길동";
        User user = new User(userName);
        ReflectionTestUtils.setField(user, "userId", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        User result = userService.findUserById(userId);

        // then
        then(userRepository).should().findById(userId);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUserName()).isEqualTo(userName);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 Exception 을 발생시킨다.")
    void findUserById_UserNotFound() {
        // given
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.findUserById(userId)
        );

        then(userRepository).should().findById(userId);
        assertThat(exception.getResponseCodeEnum()).isEqualTo(ResponseCodeEnum.USER_NOT_FOUND);
    }
}