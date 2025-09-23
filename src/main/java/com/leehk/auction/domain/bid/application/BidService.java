package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.global.response.CustomException;

import java.util.List;

public interface BidService {

    /**
     * 지정된 경매에 대해 주어진 입찰자와 입찰가로 입찰을 진행합니다.
     * 입찰가는 현재 경매 가격보다 높아야 하며 경매가 진행 중이어야 합니다.
     *
     * @param auctionId 입찰이 이루어질 경매의 ID
     * @param bidderId  입찰을 진행하는 입찰자의 ID
     * @param bidPrice  입찰 금액
     * @return 성공적으로 입찰된 {@code Bid} 객체
     * @throws CustomException 경매가 이미 종료되었거나 입찰가가 너무 낮은 경우
     */
    Bid placeBid(Long auctionId, Long bidderId, long bidPrice);

    /**
     * 지정된 경매 ID와 관련된 입찰 목록을 조회합니다.
     * 이 메서드는 주어진 경매와 관련된 모든 입찰을 가져와서
     * 해당 경매의 입찰 활동을 확인할 수 있게 합니다.
     *
     * @param auctionId 입찰을 조회할 경매의 ID
     * @return 지정된 경매 ID와 관련된 {@code Bid} 객체 목록
     */
    List<Bid> getBidByAuctionId(Long auctionId);

    /**
     * 주어진 입찰 ID로 입찰 정보를 조회합니다.
     * 해당 ID의 입찰이 없는 경우 예외가 발생합니다.
     *
     * @param bidId 조회할 입찰의 고유 식별자
     * @return 주어진 ID에 해당하는 {@code Bid} 객체
     * @throws CustomException 해당 ID의 입찰을 찾을 수 없는 경우
     */
    Bid getBidByBidId(Long bidId);

    /**
     * 특정 경매의 최고 입찰가를 조회합니다.
     * 이 메서드는 주어진 경매 ID와 연관된 최고 입찰 정보를 가져옵니다.
     *
     * @param auctionId 최고 입찰가를 조회할 경매의 고유 식별자
     * @return 해당 경매의 최고 입찰 정보를 담은 {@code Bid} 객체
     * @throws CustomException 해당 경매의 입찰 정보를 찾을 수 없는 경우
     */
    Bid getHighestBid(Long auctionId);

    /**
     * 주어진 입찰 ID와 입찰자 ID를 기반으로 입찰을 취소합니다.
     * 입찰 ID가 존재하지 않거나 입찰자 ID가 입찰 소유자와 일치하지 않는 경우 예외가 발생합니다.
     * 취소가 성공적으로 이루어지면 시스템에서 해당 입찰이 제거됩니다.
     *
     * @param bidId    취소할 입찰의 고유 식별자
     * @param bidderId 입찰을 취소하려는 입찰자의 고유 식별자
     * @throws CustomException 입찰이 존재하지 않거나 입찰자가 취소 권한이 없는 경우
     */
    void cancelBid(Long bidId, Long bidderId);
}