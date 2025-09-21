package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionDto;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;

import java.util.List;

public interface AuctionService {

    /**
     * 특정 경매를 고유 식별자로 조회합니다.
     *
     * @param auctionId 조회할 경매의 고유 식별자
     * @return 주어진 식별자와 연관된 {@link AuctionDto}
     * @throws com.leehk.auction.global.response.CustomException 지정된 식별자의 경매를 찾을 수 없는 경우
     */
    Auction getAuction(Long auctionId);

    /**
     * 진행 중인 모든 경매 목록을 조회합니다.
     *
     * @return 진행 중인 경매를 나타내는 {@link Auction} 목록
     */
    List<Auction> getOngoingAuctions();

    /**
     * 주어진 경매 정보로 새로운 경매를 생성하고 데이터베이스에 저장합니다.
     *
     * @param auction 생성할 경매의 정보를 포함하는 {@link Auction} 객체
     * @return 새로 생성된 경매의 {@link Auction}
     */
    Auction createAuction(Auction auction);

    /**
     * 고유 식별자로 식별되는 기존 경매의 정보를 업데이트합니다.
     * 경매의 필드들이 제공된 정보로 업데이트되고 변경 사항이 저장됩니다.
     *
     * @param auctionId 업데이트할 경매의 고유 식별자
     * @param auction   업데이트된 경매 정보를 포함하는 {@link Auction}
     * @return 변경 사항이 저장된 후의 업데이트된 {@link Auction}
     * @throws com.leehk.auction.global.response.CustomException 지정된 식별자의 경매를 찾을 수 없는 경우
     */
    Auction updateAuction(Long auctionId, Auction auction);

    /**
     * 고유 식별자로 식별되는 기존 경매를 삭제합니다.
     *
     * @param auctionId 삭제할 경매의 고유 식별자
     * @throws com.leehk.auction.global.response.CustomException 지정된 식별자의 경매를 찾을 수 없는 경우
     */
    void deleteAuction(Long auctionId);

    /**
     * 지정된 경매에 새로운 입찰을 진행합니다.
     *
     * @param auctionId 입찰이 이루어지는 경매의 고유 식별자
     * @param bidPrice  입찰 가격
     * @return 입찰 후 경매의 업데이트된 상태를 포함하는 {@link Auction}
     * @throws com.leehk.auction.global.response.CustomException 경매를 찾을 수 없거나, 경매가 이미 종료되었거나,
     *                                                           입찰가가 현재 가격보다 낮은 경우
     */
    Auction placeBid(Long auctionId, long bidPrice);

    /**
     * 지정된 경매를 종료 처리합니다.
     *
     * @param auctionId 종료할 경매의 고유 식별자
     * @return 종료된 경매의 정보를 포함하는 {@link Auction}
     * @throws com.leehk.auction.global.response.CustomException 지정된 식별자의 경매를 찾을 수 없거나
     *                                                           이미 종료된 경매인 경우
     */
    Auction endAuction(Long auctionId);
}