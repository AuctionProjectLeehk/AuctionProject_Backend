package com.leehk.auction.domain.bid.presentation;

import com.leehk.auction.domain.bid.application.BidService;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.dto.BidRequestDto;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import com.leehk.auction.global.auth.CustomUserDetails;
import com.leehk.auction.global.response.ApiResponse;
import com.leehk.auction.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bid")
public class BidController {

    private final BidService bidService;

    /**
     * 사용자가 특정 경매에 대해 새로운 입찰을 생성합니다.
     *
     * @param userDetails 인증된 사용자의 정보를 포함하는 객체. 여기에서 사용자의 ID를 추출하여 입찰자 정보를 설정합니다.
     * @param bidRequestDto 입찰 정보를 담고 있는 객체. 경매 ID와 입찰 금액을 포함합니다.
     * @return 성공 코드와 함께 생성된 입찰에 대한 응답 데이터를 포함하는 ApiResponse 객체
     */
    @PostMapping
    public ApiResponse<BidResponseDto> placeBid(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BidRequestDto bidRequestDto
    ) {
        Long userId = userDetails.getUserId();

        Bid bid = bidService.placeBid(bidRequestDto.getAuctionId(), userId, bidRequestDto.getBidPrice());

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