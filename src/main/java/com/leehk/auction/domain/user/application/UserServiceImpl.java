package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.user.converter.UserConverter;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.infrastructure.UserEntity;
import com.leehk.auction.domain.user.infrastructure.UserRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserConverter.entityToDomain(userEntity);
    }

    @Override
    public User getUserByPublicId(UUID publicId) {
        UserEntity userEntity = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserConverter.entityToDomain(userEntity);
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserConverter.entityToDomain(userEntity);
    }

    @Override
    public User getUserByNickname(String nickname) {
        UserEntity userEntity = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserConverter.entityToDomain(userEntity);
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = UserConverter.domainToEntity(user);

        UserEntity savedUserEntity = userRepository.save(userEntity);

        return UserConverter.entityToDomain(savedUserEntity);
    }
}
