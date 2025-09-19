package com.leehk.auction.domain.auction.domain;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Auction {

    private Long id;
    private String title;
    private String description;
    private long startPrice;
    private long currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;

    // 입찰 처리
    public void placeBid(long bidPrice) {
        validateBid(bidPrice);
        this.currentPrice = bidPrice;
    }

    // 입찰 유효성 확인
    public void validateBid(long bidPrice) {
        // 경매가 진행중인지 확인
        if (status != AuctionStatus.ONGOING) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }
        
        // 가격이 현재가보다 높은지 확인
        if (bidPrice <= currentPrice) {
            throw new CustomException(ErrorCode.BID_TOO_LOW);
        }
    }

    // 경매 종료 처리
    public void endAuction() {
        if (status == AuctionStatus.ENDED) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        this.status = AuctionStatus.ENDED;
    }

    // 수정 로직
    public void updateAuction(String title, String description, long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        if (status == AuctionStatus.ENDED) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        this.title = title;
        this.description = description;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
