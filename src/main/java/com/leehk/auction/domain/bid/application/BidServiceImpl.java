package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import com.leehk.auction.domain.bid.infrastructure.BidRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;

    @Override
    @Transactional
    public Bid placeBid(Long auctionId, Long bidderId, long bidPrice) {
        // BidService는 AuctionService를 통해 입찰 처리
        Auction auction = auctionService.placeBid(auctionId, bidderId, bidPrice);

        return auction.getHighestBid();
    }

    @Override
    public List<Bid> getBidByAuctionId(Long auctionId) {
        return bidRepository.findByAuctionEntity_Id(auctionId)
                .stream()
                .map(BidConverter::entityToDomain)
                .toList();
    }

    @Override
    public Bid getBidByBidId(UUID bidId) {
        return bidRepository.findById(bidId)
                .map(BidConverter::entityToDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));
    }

    @Override
    public Bid getHighestBid(Long auctionId) {
        return bidRepository.findTopByAuctionEntity_IdOrderByBidPriceDesc(auctionId)
                .map(BidConverter::entityToDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));
    }

    @Override
    public void cancelBid(UUID bidId, Long bidderId) {
        BidEntity bidEntity = bidRepository.findById(bidId)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        if (!bidEntity.getBidderId().equals(bidderId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_BID_ACTION);
        }

        bidRepository.delete(bidEntity);
    }
}
