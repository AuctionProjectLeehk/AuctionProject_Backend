package com.leehk.auction.domain.bid.presentation;

import com.leehk.auction.domain.bid.application.BidService;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.dto.BidRequestDto;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import com.leehk.auction.global.response.ApiResponse;
import com.leehk.auction.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bid")
public class BidController {

    private final BidService bidService;

    /**
     * 입찰자 ID와 입찰 금액으로 지정된 경매에 입찰을 진행합니다.
     *
     * @param bidRequestDto 경매 ID, 입찰자 ID, 입찰 금액을 포함하는 입찰 세부 정보
     * @return 성공적으로 입찰된 세부 정보를 BidResponseDto로 포함하는 ApiResponse
     */
    @PostMapping
    public ApiResponse<BidResponseDto> placeBid(
            @RequestBody BidRequestDto bidRequestDto
    ) {
        Bid bid = bidService.placeBid(bidRequestDto.getAuctionId(), bidRequestDto.getBidderId(), bidRequestDto.getBidPrice());

        return ApiResponse.success(SuccessCode.OK, BidConverter.domainToDto(bid));
    }

    /**
     * 지정된 경매 ID와 연관된 입찰 목록을 조회합니다.
     *
     * @param auctionId 입찰 목록을 조회할 경매의 ID
     * @return 지정된 경매에 대한 입찰을 나타내는 BidResponseDto 목록을 포함하는 ApiResponse
     */
    @GetMapping("/{auctionId}")
    public ApiResponse<List<BidResponseDto>> getBidByAuctionId(
            @PathVariable Long auctionId
    ) {


        return ApiResponse.success(SuccessCode.OK, bidService.getBidByAuctionId(auctionId)
                .stream()
                .map(BidConverter::domainToDto)
                .toList());
    }
}