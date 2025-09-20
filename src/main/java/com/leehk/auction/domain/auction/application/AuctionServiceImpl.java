package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionDto;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;

    @Override
    public AuctionDto getAuction(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionConverter.DomainToDto(AuctionConverter.EntityToDomain(auctionEntity));
    }

    @Override
    public List<AuctionDto> getOngoingAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ONGOING)
                .stream()
                .map(AuctionConverter::EntityToDomain)
                .map(AuctionConverter::DomainToDto)
                .toList();
    }

    @Override
    @Transactional
    public AuctionDto createAuction(Auction auction) {
        AuctionEntity auctionEntity = AuctionConverter.DomainToEntity(auction);

        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        return AuctionConverter.DomainToDto(AuctionConverter.EntityToDomain(savedAuctionEntity));
    }

    @Override
    @Transactional
    public AuctionDto updateAuction(Long auctionId, Auction Auction) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        auctionEntity.updateFromDomain(Auction);
        return AuctionConverter.DomainToDto(AuctionConverter.EntityToDomain(auctionEntity));
    }

    @Override
    @Transactional
    public void deleteAuction(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        auctionRepository.delete(auctionEntity);
    }

    @Override
    @Transactional
    public AuctionDto placeBid(Long auctionId, long bidPrice) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.EntityToDomain(auctionEntity);
        auction.placeBid(auctionId, bidPrice);
        auctionEntity.updateFromDomain(auction);

        return AuctionConverter.DomainToDto(auction);
    }

    @Override
    public AuctionDto endAuction(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.EntityToDomain(auctionEntity);
        auction.endAuction();
        auctionEntity.updateFromDomain(auction);

        return AuctionConverter.DomainToDto(auction);
    }
}
