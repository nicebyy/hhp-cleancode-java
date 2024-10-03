package hhplus.user.infrastructure;

import hhplus.common.config.jpa.QueryDslConfig;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserRepositoryImpl.class})  // 필요한 리포지토리 구현체와 설정 임포트
@ActiveProfiles("test")  // 'test' 프로파일 활성화
class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp(){
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("존재하는 사용자 ID로 사용자를 조회한다.")
    void findById_existingUser_returnsUser() {
        // given
        User user = new User("이순신");
        User savedUser = userJpaRepository.save(user);

        // when
        Optional<User> result = userRepository.findById(savedUser.getUserId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(result.get().getUserName()).isEqualTo("이순신");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 사용자를 조회하면 빈 Optional 을 반환한다.")
    void findById_nonExistingUser_returnsEmpty() {
        // given
        Long nonExistingUserId = 999L;

        // when
        Optional<User> result = userRepository.findById(nonExistingUserId);

        // then
        assertThat(result).isNotPresent();
    }
}