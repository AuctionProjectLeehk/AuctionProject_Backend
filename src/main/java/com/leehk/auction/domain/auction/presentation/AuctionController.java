package com.leehk.auction.domain.auction.presentation;

import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionRequestDto;
import com.leehk.auction.domain.auction.dto.AuctionResponseDto;
import com.leehk.auction.global.response.ApiResponse;
import com.leehk.auction.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Auction API", description = "경매 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

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
    public ApiResponse<AuctionResponseDto> createAuction(@RequestBody AuctionRequestDto auctionDto) {
        Auction auction = AuctionConverter.dtoToDomain(auctionDto);

        return ApiResponse.success(SuccessCode.CREATED,
                AuctionConverter.domainToDto(auctionService.createAuction(auction)));
    }

    @PutMapping("/{auctionId}")
    public ApiResponse<AuctionResponseDto> updateAuction(@PathVariable Long auctionId, @RequestBody AuctionRequestDto auctionDto) {
        Auction auction = AuctionConverter.dtoToDomain(auctionDto);

        return ApiResponse.success(SuccessCode.UPDATED,
                AuctionConverter.domainToDto(auctionService.updateAuction(auctionId, auction)));
    }

    @DeleteMapping("/{auctionId}")
    public ApiResponse<Void> deleteAuction(@PathVariable Long auctionId) {
        auctionService.deleteAuction(auctionId);
        return ApiResponse.success(SuccessCode.DELETED, null);
    }
}
