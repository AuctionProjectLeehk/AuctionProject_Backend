package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.bid.converter.AutoBidConverter;
import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.domain.bid.infrastructure.AutoBidEntity;
import com.leehk.auction.domain.bid.infrastructure.AutoBidRepository;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutoBidServiceImpl implements AutoBidService {

    private final AutoBidRepository autoBidRepository;
    private final UserService userService;
    private final AuctionService auctionService;

    @Override
    public AutoBid getAutoBidByAuctionIdAndUserId(Long auctionId, Long autoBidderId) {
        if (!auctionService.existsAuctionById(auctionId)) {
            throw new CustomException(ErrorCode.AUCTION_NOT_FOUND);
        }

        if (!userService.isUserExistById(autoBidderId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Optional<AutoBidEntity> autoBid = autoBidRepository.findByAuctionEntity_IdAndAutoBidderId(auctionId, autoBidderId);
        if (autoBid.isEmpty()) {
            throw new CustomException(ErrorCode.AUTO_BID_NOT_FOUND);
        }

        return AutoBidConverter.entityToDomain(autoBid.get());
    }

    @Override
    public List<AutoBid> getAutoBidsByAuctionId(Long auctionId) {
        if (!auctionService.existsAuctionById(auctionId)) {
            throw new CustomException(ErrorCode.AUCTION_NOT_FOUND);
        }

        return autoBidRepository.findActiveByAuctionEntity_Id(auctionId)
                .stream()
                .map(AutoBidConverter::entityToDomain)
                .toList();
    }

    @Override
    public AutoBid getAutoBidById(UUID autoBidId) {
        return autoBidRepository.findById(autoBidId)
                .map(AutoBidConverter::entityToDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));
    }

    @Override
    @Transactional
    public AutoBid registerAutoBid(Long auctionId, Long userId, long maxAutoBidPrice) {
        Auction auction = auctionService.registerAutoBid(auctionId, userId, maxAutoBidPrice);

        return auction.getAutoBids().stream()
                .filter(autoBid -> autoBid.getAutoBidderId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deactivateAutoBid(UUID autoBidId, Long auctionId, Long autoBidderId) {
        if (!auctionService.existsAuctionById(auctionId)) {
            throw new CustomException(ErrorCode.AUCTION_NOT_FOUND);
        }

        if (!userService.isUserExistById(autoBidderId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        AutoBidEntity autoBidEntityById = autoBidRepository.findById(autoBidId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));

        AutoBidEntity autoBidEntityByAuctionAndUser = autoBidRepository.findByAuctionEntity_IdAndAutoBidderId(auctionId, autoBidderId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));

        if (!autoBidEntityById.getId().equals(autoBidEntityByAuctionAndUser.getId())) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_ACCESS);
        }

        auctionService.deactivateAutoBidByUserId(auctionId, autoBidderId);
    }
}
