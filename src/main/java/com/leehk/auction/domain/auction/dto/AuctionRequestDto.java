package com.leehk.auction.domain.auction.dto;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AuctionRequestDto {

    private String title;
    private String description;
    private long startPrice;
    private long currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}
