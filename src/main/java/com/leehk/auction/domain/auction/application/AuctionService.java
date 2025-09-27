package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.global.response.CustomException;

import java.util.List;
import java.util.UUID;

public interface AuctionService {

    /**
     * 특정 경매를 고유 식별자로 조회합니다.
     *
     * @param auctionId 조회할 경매의 고유 식별자
     * @return 주어진 식별자와 연관된 {@link Auction}
     * @throws CustomException 지정된 식별자의 경매를 찾을 수 없는 경우
     */
    Auction getAuction(Long auctionId);

    /**
     * 진행 중인 모든 경매 목록을 조회합니다.
     *
     * @return 진행 중인 경매를 나타내는 {@link Auction} 목록
     */
    List<Auction> getOngoingAuctions();

    /**
     * 새로운 경매를 생성하고 지정된 사용자와 연결합니다.
     *
     * @param auction 생성할 경매의 세부 정보를 포함하는 {@link Auction} 객체
     * @param userId  경매를 생성하는 사용자의 고유 식별자
     * @return 고유 ID를 포함한 업데이트된 세부 정보가 포함된 생성된 {@link Auction} 객체
     */
    Auction createAuction(Auction auction, Long userId);

    /**
     * 기존 경매의 세부 정보를 업데이트합니다.
     * 이 작업은 경매 업데이트를 허용하고 업데이트를 수행하는 사용자가 필요한 권한을 가지고 있는지 확인합니다.
     *
     * @param auctionId     업데이트할 경매의 고유 식별자
     * @param upatedAuction 경매에 대한 업데이트된 세부 정보를 포함하는 {@link Auction} 객체
     * @param userId        경매 업데이트를 시도하는 사용자의 고유 식별자
     * @return 최신 세부 정보가 포함된 업데이트된 {@link Auction} 객체
     * @throws CustomException 경매를 찾을 수 없거나, 사용자에게 경매를 업데이트할 권한이 없거나,
     *                         경매를 업데이트할 수 없는 경우
     */
    Auction updateAuction(Long auctionId, Auction upatedAuction, Long userId);

    /**
     * 고유 ID로 식별된 경매를 삭제합니다. 이 메서드는 경매를 삭제하려는 사용자가
     * 적절한 권한을 가지고 있는지 확인합니다.
     *
     * @param auctionId 삭제할 경매의 고유 식별자
     * @param userId    경매 삭제를 시도하는 사용자의 고유 식별자
     * @throws CustomException 경매를 찾을 수 없거나 사용자에게 적절한 권한이 없는 경우
     */
    void deleteAuction(Long auctionId, Long userId);

    /**
     * 지정된 경매에 새로운 입찰을 진행합니다.
     *
     * @param auctionId 입찰이 이루어지는 경매의 고유 식별자
     * @param bidPrice  입찰 가격
     * @return 입찰 후 경매의 업데이트된 상태를 포함하는 {@link Auction}
     * @throws CustomException 경매를 찾을 수 없거나, 경매가 이미 종료되었거나,
     *                         입찰가가 현재 가격보다 낮은 경우
     */
    Auction placeBid(Long auctionId, Long bidderId, long bidPrice);

    /**
     * 주어진 경매 ID, 입찰 ID, 입찰자 ID와 관련된 입찰을 취소합니다.
     * 해당 입찰을 경매에서 제거하고 경매의 현재 가격을 업데이트합니다.
     *
     * @param auctionId 취소할 입찰이 포함된 경매의 고유 식별자
     * @param bidId     취소할 입찰의 고유 식별자
     * @param bidderId  입찰을 진행한 입찰자의 고유 식별자
     * @return 입찰 취소 후 업데이트된 {@link Auction} 객체
     * @throws CustomException 경매나 입찰을 찾을 수 없거나,
     *                         입찰자가 해당 입찰을 취소할 권한이 없는 경우
     */
    Auction cancelBid(Long auctionId, UUID bidId, Long bidderId);

    /**
     * 지정된 경매를 종료 처리합니다.
     *
     * @param auctionId 종료할 경매의 고유 식별자
     * @return 종료된 경매의 정보를 포함하는 {@link Auction}
     * @throws CustomException 지정된 식별자의 경매를 찾을 수 없거나
     *                         이미 종료된 경매인 경우
     */
    Auction endAuction(Long auctionId);
}