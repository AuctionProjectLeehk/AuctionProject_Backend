package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;

    @Override
    public Auction getAuction(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionConverter.entityToDomain(auctionEntity);
    }

    @Override
    public boolean existsAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId).isPresent();
    }

    @Override
    public List<Auction> getOngoingAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ONGOING)
                .stream()
                .map(AuctionConverter::entityToDomain)
                .toList();
    }

    @Override
    @Transactional
    public Auction createAuction(Auction auction, Long userId) {
        User user = userService.getUserById(userId);

        auction.assignOwner(user);

        AuctionEntity auctionEntity = AuctionConverter.domainToEntity(auction);

        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        return AuctionConverter.entityToDomain(savedAuctionEntity);
    }

    @Override
    @Transactional
    public Auction updateAuction(Long auctionId, Auction updatedAuction, Long userId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));


        if (!auctionEntity.getOwnerEntity().getId().equals(userId))
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUCTION_ACTION);

        auctionEntity.updateFromDomain(updatedAuction);
        return AuctionConverter.entityToDomain(auctionEntity);
    }

    @Override
    public void deleteAuction(Long auctionId, Long userId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        if (!auctionEntity.getOwnerEntity().getId().equals(userId))
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUCTION_ACTION);

        auctionRepository.delete(auctionEntity);
    }

    @Override
    @Transactional
    public Auction placeBid(Long auctionId, Long bidderId, long bidPrice) {
        AuctionEntity auctionEntity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.entityToDomain(auctionEntity);

        auction.placeBid(bidderId, bidPrice);

        auctionEntity.updateFromDomain(auction);

        auctionRepository.saveAndFlush(auctionEntity);

        return auction;
    }

    @Override
    @Transactional
    public Auction cancelBid(Long auctionId, UUID bidId, Long bidderId) {
        AuctionEntity auctionEntity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        // domain
        Auction auction = AuctionConverter.entityToDomain(auctionEntity);

        // domain에서 cancel
        auction.cancelBid(bidId, bidderId);

        // entity 수정
        auctionEntity.updateFromDomain(auction);

        auctionRepository.saveAndFlush(auctionEntity);

        return auction;
    }

    @Override
    @Transactional
    public Auction endAuction(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.entityToDomain(auctionEntity);
        auction.endAuction();
        auctionEntity.updateFromDomain(auction);

        return auction;
    }

    @Override
    @Transactional
    public Auction registerAutoBid(Long auctionId, Long userId, long maxAutoBidPrice) {
        AuctionEntity auctionEntity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        if (!userService.isUserExistById(userId))
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Auction auction = AuctionConverter.entityToDomain(auctionEntity);

        auction.registerAutoBid(userId, maxAutoBidPrice);

        auctionEntity.updateFromDomain(auction);

        auctionRepository.saveAndFlush(auctionEntity);

        return auction;
    }

    @Override
    @Transactional
    public void executeAutoBids(Long auctionId) {
        AuctionEntity auctionEntity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.entityToDomain(auctionEntity);
        auction.executeAutoBids();

        auctionEntity.updateFromDomain(auction);
        auctionRepository.saveAndFlush(auctionEntity);
    }

    @Override
    @Transactional
    public void deactivateAutoBidByUserId(Long auctionId, Long userId) {
        AuctionEntity entity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        if (userService.getUserById(userId) == null)
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Auction auction = AuctionConverter.entityToDomain(entity);
        auction.deactivateAutoBidByUserId(userId);

        entity.updateFromDomain(auction);
        auctionRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void deactivateAutoBidById(Long auctionId, UUID autoBidId) {
        AuctionEntity entity = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Auction auction = AuctionConverter.entityToDomain(entity);
        auction.deactivateAutoBidByAutoBidId(autoBidId);

        entity.updateFromDomain(auction);
        auctionRepository.saveAndFlush(entity);
    }
}
