package com.leehk.auction.domain.user.domain;

import com.leehk.auction.domain.bid.domain.Bid;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class User {

    private Long id;
    private UUID publicId;
    private String email;
    private String name;
    private String password;
    private String nickname;

    @Builder.Default
    private LocalDateTime joinDate = LocalDateTime.now();

    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        this.bids.add(bid);
    }
}
