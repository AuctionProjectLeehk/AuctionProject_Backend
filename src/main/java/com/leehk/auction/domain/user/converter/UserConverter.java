package com.leehk.auction.domain.user.converter;

import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.infrastructure.UserEntity;

public class UserConverter {

    public static User entityToDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .publicId(userEntity.getPublicId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .nickname(userEntity.getNickname())
                .joinDate(userEntity.getJoinDate())
                .build();
    }
    
    public static UserEntity domainToEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .joinDate(user.getJoinDate())
                .build();
    }
}
