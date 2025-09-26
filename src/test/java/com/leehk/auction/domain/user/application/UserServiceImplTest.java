package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

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
    @DisplayName("유저 저장 및 조회 성공 테스트")
    public void saveAndFindUserTest() {
        // given
        User createdUser = userService.saveUser(testUser);

        // when
        User userByEmail = userService.getUserByEmail(testUser.getEmail());
        User userByNickname = userService.getUserByNickname(testUser.getNickname());

        // then
        assertThat(userByEmail.getPublicId()).isEqualTo(createdUser.getPublicId());
        assertThat(userByNickname.getPublicId()).isEqualTo(createdUser.getPublicId());
    }
    
    @Test
    @DisplayName("유저 조회 실패")
    public void getUserByEmail_Fail() {
        // given
        User createdUser = userService.saveUser(testUser);

        // when and then
        assertThatThrownBy(() -> userService.getUserById(-1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}