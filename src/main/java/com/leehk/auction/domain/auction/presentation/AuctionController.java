package com.leehk.auction.domain.auction.presentation;

import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionRequestDto;
import com.leehk.auction.domain.auction.dto.AuctionResponseDto;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.auth.CustomUserDetails;
import com.leehk.auction.global.response.ApiResponse;
import com.leehk.auction.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Auction API", description = "경매 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final UserService userService;

    @Operation(summary = "특정 경매 조회", description = "경매 ID로 상세 정보를 가져옵니다")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
    )
    @GetMapping("/{auctionId}")
    public ApiResponse<AuctionResponseDto> getAuction(@PathVariable Long auctionId) {
        return ApiResponse.success(SuccessCode.OK,
                AuctionConverter.domainToDto(auctionService.getAuction(auctionId)));
    }

    @GetMapping("/ongoing")
    public ApiResponse<List<AuctionResponseDto>> getOngoingAuctions() {
        List<AuctionResponseDto> ongoingAuctionDtoList = auctionService.getOngoingAuctions()
                .stream()
                .map(AuctionConverter::domainToDto)
                .toList();

        return ApiResponse.success(SuccessCode.OK, ongoingAuctionDtoList);
    }

    @PostMapping
    public ApiResponse<AuctionResponseDto> createAuction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AuctionRequestDto auctionDto
    ) {
        try {
            System.out.println("auctionDto = " + auctionDto);

            Long userId = userDetails.getUserId();

            System.out.println("userId = " + userId);
            User user = userService.getUserById(userId);

            Auction auction = AuctionConverter.dtoToDomain(auctionDto, user);

            return ApiResponse.success(SuccessCode.CREATED,
                    AuctionConverter.domainToDto(auctionService.createAuction(auction, userId)));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/{auctionId}")
    public ApiResponse<AuctionResponseDto> updateAuction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long auctionId,
            @RequestBody AuctionRequestDto auctionDto
    ) {
        Long userId = userDetails.getUserId();
        User user = userService.getUserById(userId);

        Auction auction = AuctionConverter.dtoToDomain(auctionDto, user);

        return ApiResponse.success(SuccessCode.UPDATED,
                AuctionConverter.domainToDto(auctionService.updateAuction(auctionId, auction, userId)));
    }

    @DeleteMapping("/{auctionId}")
    public ApiResponse<Void> deleteAuction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long auctionId
    ) {
        Long userId = userDetails.getUserId();
        auctionService.deleteAuction(auctionId, userId);
        return ApiResponse.success(SuccessCode.DELETED, null);
    }
}
