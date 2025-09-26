package com.leehk.auction.domain.user.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByPublicId(UUID publicId);

    void delete(UserEntity userEntity);
}
