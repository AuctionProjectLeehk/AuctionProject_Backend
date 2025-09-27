package com.leehk.auction.domain.user.infrastructure;

import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID publicId;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @OneToMany(mappedBy = "ownerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AuctionEntity> auctionEntities = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.publicId = UUID.randomUUID();
        this.joinDate = LocalDateTime.now();
    }
}
