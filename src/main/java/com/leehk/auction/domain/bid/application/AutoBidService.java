package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.global.response.CustomException;

import java.util.List;
import java.util.UUID;

/**
 * 자동 입찰(AutoBid) 기능을 관리하는 서비스 인터페이스입니다.
 * 사용자는 경매에 대해 자동 입찰을 등록, 조회, 비활성화할 수 있습니다.
 *
 * <p>자동 입찰은 사용자가 지정한 최대 금액 한도 내에서 시스템이 자동으로 입찰을 진행하는 기능입니다.</p>
 */
public interface AutoBidService {

    /**
     * 특정 경매에 대해 주어진 사용자 ID로 등록된 자동 입찰 정보를 조회합니다.
     *
     * @param auctionId  자동 입찰이 설정된 경매의 ID
     * @param userId     자동 입찰을 설정한 사용자의 ID
     * @return 해당 경매와 사용자에 대응하는 {@code AutoBid} 객체
     * @throws CustomException 경매 또는 사용자가 존재하지 않거나, 자동 입찰 정보가 없는 경우
     */
    AutoBid getAutoBidByAuctionIdAndUserId(Long auctionId, Long userId);

    /**
     * 특정 경매에 등록된 모든 활성 자동 입찰 목록을 조회합니다.
     *
     * @param auctionId 자동 입찰을 조회할 경매의 ID
     * @return 지정된 경매에 대해 활성 상태인 {@code AutoBid} 객체 목록
     * @throws CustomException 해당 경매가 존재하지 않는 경우
     */
    List<AutoBid> getAutoBidsByAuctionId(Long auctionId);

    /**
     * 자동 입찰의 고유 ID(UUID)를 기반으로 해당 자동 입찰 정보를 조회합니다.
     *
     * @param autoBidId 조회할 자동 입찰의 고유 식별자(UUID)
     * @return 주어진 ID에 해당하는 {@code AutoBid} 객체
     * @throws CustomException 해당 ID의 자동 입찰을 찾을 수 없는 경우
     */
    AutoBid getAutoBidById(UUID autoBidId);

    /**
     * 특정 경매에 대해 사용자의 자동 입찰을 등록합니다.
     *
     * <p>등록된 자동 입찰은 사용자가 지정한 최대 입찰가까지 시스템이 자동으로 입찰을 수행하도록 설정됩니다.</p>
     *
     * @param auctionId       자동 입찰을 설정할 경매의 ID
     * @param userId          자동 입찰을 등록할 사용자의 ID
     * @param maxAutoBidPrice 자동 입찰 시 설정할 최대 입찰 금액
     * @return 성공적으로 등록된 {@code AutoBid} 객체
     * @throws CustomException 경매 또는 사용자가 존재하지 않거나, 등록 과정에서 오류가 발생한 경우
     */
    AutoBid registerAutoBid(Long auctionId, Long userId, long maxAutoBidPrice);

    /**
     * 특정 자동 입찰을 비활성화합니다.
     *
     * <p>이 메서드는 사용자가 지정한 자동 입찰을 중단하며,
     * 비활성화된 자동 입찰은 더 이상 경매에서 자동으로 입찰되지 않습니다.</p>
     *
     * @param autoBidId     비활성화할 자동 입찰의 고유 식별자(UUID)
     * @param auctionId     자동 입찰이 속한 경매의 ID
     * @param autoBidderId  자동 입찰을 등록한 사용자의 ID
     * @throws CustomException 경매 또는 사용자가 존재하지 않거나, 자동 입찰 접근 권한이 유효하지 않은 경우
     */
    void deactivateAutoBid(UUID autoBidId, Long auctionId, Long autoBidderId);
}
