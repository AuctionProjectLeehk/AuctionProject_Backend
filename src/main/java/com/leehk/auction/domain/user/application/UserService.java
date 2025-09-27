package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;

import java.util.UUID;

public interface UserService {

    /**
     * 사용자를 고유 식별자로 조회합니다.
     *
     * @param userId 조회할 사용자의 고유 식별자
     * @return 주어진 ID에 해당하는 사용자 객체
     * @throws CustomException 주어진 ID로 사용자를 찾을 수 없는 경우
     */
    User getUserById(Long userId);

    /**
     * 사용자가 고유 식별자로 존재하는지 확인합니다.
     *
     * @param userId 존재 여부를 확인할 사용자의 고유 식별자
     * @return 해당 ID를 가진 사용자가 존재하면 true, 아니면 false
     */
    boolean isUserExistById(Long userId);

    /**
     * 사용자를 공개 식별자로 조회합니다.
     *
     * @param publicId 조회할 사용자의 공개 UUID 식별자
     * @return 주어진 공개 식별자에 해당하는 사용자 객체
     * @throws CustomException 주어진 공개 식별자로 사용자를 찾을 수 없는 경우
     */
    User getUserByPublicId(UUID publicId);

    /**
     * 사용자를 이메일 주소로 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return 주어진 이메일 주소에 해당하는 사용자 객체
     * @throws CustomException 주어진 이메일 주소로 사용자를 찾을 수 없는 경우
     */
    User getUserByEmail(String email);

    /**
     * 제공된 이메일로 사용자가 존재하는지 확인합니다.
     *
     * @param email 존재 여부를 확인할 이메일 주소
     * @return 해당 이메일을 가진 사용자가 존재하면 true, 아니면 false
     */
    boolean isUserExistByEmail(String email);

    /**
     * 사용자를 닉네임으로 조회합니다.
     *
     * @param nickname 조회할 사용자의 닉네임
     * @return 주어진 닉네임에 해당하는 사용자 객체
     * @throws CustomException 주어진 닉네임으로 사용자를 찾을 수 없는 경우
     */
    User getUserByNickname(String nickname);

    /**
     * 사용자 엔티티를 데이터베이스에 저장합니다.
     *
     * @param user 저장할 사용자 정보를 포함하는 {@link User} 객체
     * @return 저장된 후의 업데이트된 {@link User} 객체
     */
    User saveUser(User user);

//    /**
//     * 사용자의 비밀번호를 업데이트합니다.
//     *
//     * @param userId      비밀번호를 변경할 사용자의 고유 식별자
//     * @param newPassword 새로운 비밀번호
//     * @return 비밀번호가 업데이트된 사용자 객체
//     * @throws CustomException 주어진 ID로 사용자를 찾을 수 없는 경우
//     */
//    User updatePassword(Long userId, String newPassword);
}
