package com.leehk.auction.domain.auction.dto;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AuctionResponseDto {

    private Long id;
    private String title;
    private String description;
    private long startPrice;
    private long currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private List<BidResponseDto> bids;
}
