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
     * @param bidderId 입찰을 진행하는 입찰자의 ID
     * @param bidPrice 입찰 금액
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
}
