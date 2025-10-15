package com.leehk.auction.domain.user.domain;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.wallet.domain.Wallet;
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
    private List<Auction> auctions = new ArrayList<>();

    @Builder.Default
    private List<Wallet> wallets = new ArrayList<>();

    public void addAuction(Auction auction) {
        this.auctions.add(auction);
    }

    public void updateWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    public void addWallet(Wallet wallet) {
        this.wallets.add(wallet);
    }
}
