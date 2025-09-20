package com.leehk.auction.domain.auction.dto;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionDto {

    private Long id;
    private String title;
    private String description;
    private long startPrice;
    private long currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}
