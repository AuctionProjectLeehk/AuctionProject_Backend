package com.leehk.auction.domain.user.infrastructure;

import com.leehk.auction.domain.user.converter.UserConverter;
import com.leehk.auction.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .email("<EMAIL>")
                .name("test")
                .password("<PASSWORD>")
                .nickname("test")
                .build();
    }

    @Test
    @DisplayName("회원 저장 및 조회 테스트")
    void saveAndFindUserTest() {
        // given
        UserEntity createdUserEntity = userRepository.save(UserConverter.domainToEntity(testUser));

        // when
        Optional<UserEntity> userEntityByEmail = userRepository.findByEmail(testUser.getEmail());
        Optional<UserEntity> userEntityByNickname = userRepository.findByNickname(testUser.getNickname());

        // then
        assertThat(userEntityByEmail).isPresent();
        assertThat(userEntityByEmail.get().getPublicId()).isEqualTo(createdUserEntity.getPublicId());

        assertThat(userEntityByNickname).isPresent();
        assertThat(userEntityByNickname.get().getPublicId()).isEqualTo(createdUserEntity.getPublicId());
    }
}