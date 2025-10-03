package com.leehk.auction.domain.auction.domain;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
public class Auction {

    private Long id;
    private User owner;
    private String title; // 경매 제목
    private String description; // 경매 설명
    private long startPrice; // 시작가
    private long currentPrice; // 현재가
    private LocalDateTime startTime; // 시작 시간
    private LocalDateTime endTime; // 종료 시간
    private AuctionStatus status; // 경매 상태

    // 입찰 목록
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

    // 자동 입찰 목록
    @Builder.Default
    private List<AutoBid> autoBids = new ArrayList<>();

    // 경매 소유자 설정
    public void assignOwner(User user) {
        this.owner = user;
        owner.addAuction(this);
    }

    // 입찰 등록
    public Bid placeBid(Long bidderId, long bidPrice) {
        validateBid(bidPrice);
        this.currentPrice = bidPrice;

        Bid bid = Bid.create(bidderId, this.id, bidPrice);

        bids.add(bid);
        return bid;
    }

    // 입찰 취소
    public void cancelBid(UUID bidId, Long bidderId) {
        Bid bid = bids.stream()
                .filter(b -> b.getId().equals(bidId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        // 입찰자 본인 확인
        if (!bid.getBidderId().equals(bidderId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_BID_ACTION);
        }

        bids.remove(bid);

        // 현재가를 남은 입찰 중 최고가로 설정
        currentPrice = bids.stream()
                .mapToLong(Bid::getBidPrice)
                .max()
                .orElse(startPrice);
    }

    // 입찰 유효성 검사
    public void validateBid(long bidPrice) {
        // 경매 진행중 확인
        if (status != AuctionStatus.ONGOING) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        // 입찰가 검증
        if (bidPrice <= currentPrice) {
            throw new CustomException(ErrorCode.BID_TOO_LOW);
        }
    }

    // 최고 입찰자 ID 조회
    public Long getHighestBidderId() {
        return bids.stream()
                .max(Comparator.comparingLong(Bid::getBidPrice))
                .map(Bid::getBidderId)
                .orElse(null);
    }

    // 전체 입찰 목록 조회
    public List<Bid> getBids() {
        return new ArrayList<>(bids);
    }

    // 경매 종료
    public void endAuction() {
        if (status == AuctionStatus.ENDED) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        this.status = AuctionStatus.ENDED;
    }

    // 경매 정보 수정
    public void updateAuction(String title, String description, long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        if (status == AuctionStatus.ENDED) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        this.title = title;
        this.description = description;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 경매 현재가 수정
    public void updateAuctionPrice(long newPrice) {
        this.currentPrice = newPrice;
    }

    // 최고 입찰 조회
    public Bid getHighestBid() {
        return bids.stream()
                .max(Comparator.comparingLong(Bid::getBidPrice))
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));
    }

    /**
     * 자동 입찰을 등록하거나 업데이트합니다.
     * 해당 사용자의 자동 입찰이 이미 존재하면 최대 입찰가를 업데이트하고,
     * 존재하지 않으면 새로운 자동 입찰을 생성하여 목록에 추가합니다.
     *
     * @param userId          자동 입찰을 등록할 사용자 ID
     * @param maxAutoBidPrice 자동 입찰 최대 금액
     * @return 생성되거나 업데이트된 자동 입찰 객체
     */
    public AutoBid registerAutoBid(Long userId, long maxAutoBidPrice) {
        // 설정한 최대가가 해당 경매의 현재가 이하인 경우 에러처리
        if (maxAutoBidPrice <= this.currentPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_CREATE);
        }

        // 기존 자동 입찰이 있는지 확인
        Optional<AutoBid> existingAutoBid = autoBids.stream()
                .filter(ab -> ab.getAutoBidderId().equals(userId))
                .findFirst();

        if (existingAutoBid.isPresent()) {
            // 기존 자동 입찰 가격 업데이트
            AutoBid updatedAutoBid = existingAutoBid.get().updateMaxAutoBidPrice(maxAutoBidPrice);
            autoBids.remove(existingAutoBid.get());
            autoBids.add(updatedAutoBid);
            return updatedAutoBid;
        }

        AutoBid autoBid = AutoBid.create(userId, this.id, maxAutoBidPrice, Math.max(this.currentPrice, 0));
        autoBids.add(autoBid);
        return autoBid;
    }

    /**
     * 경매에 대한 자동 입찰 프로세스를 실행합니다.
     * 이 메서드는 최대 입찰가 기준 내림차순으로 모든 활성화된 자동 입찰을 처리하며,
     * 최소 입찰 단위를 준수하면서 가장 높은 유효한 입찰이 이루어지도록 합니다.
     * 이 과정에서 이루어진 모든 입찰은 경매의 현재 가격을 업데이트하고 새로운 Bid 객체를 생성합니다.
     * 여러 개의 자동 입찰이 활성화되어 있는 경우, 다음 입찰가는 두 번째로 높은 최대 입찰가에
     * 최소 입찰 단위를 조정하여 결정되며, 이를 통해 경쟁적인 입찰을 보장합니다.
     * 자동 입찰은 프로세스 결과에 따라 비활성화되거나 현재 입찰가가 적절히 조정됩니다.
     *
     * @return 경매의 자동 입찰 프로세스 중 생성된 새로운 Bid 객체들의 목록
     */
    public List<Bid> executeAutoBids() {
        int minGap = 1; // 최소 입찰 단위
        List<Bid> newBids = new ArrayList<>();

        while (true) {
            // 활성화된 자동 입찰을 최대 입찰가 기준 내림차순, 업데이트 순서 기준 정렬
            List<AutoBid> activeAutoBids = autoBids.stream()
                    .filter(AutoBid::isActive)
                    .sorted(Comparator
                            .comparingLong(AutoBid::getMaxAutoBidPrice).reversed()
                            .thenComparing(AutoBid::getUpdatedAt)
                    )
                    .toList();

            // 활성화된 자동 입찰이 없으면 종료
            if (activeAutoBids.isEmpty()) break;

            AutoBid topAutoBid = activeAutoBids.get(0);
            long nextBidPrice = this.currentPrice + minGap;

            // 활성화된 자동 입찰의 최대가가 현재가보다 높지 않으면 종료
            if (topAutoBid.getMaxAutoBidPrice() < nextBidPrice) break;

            // 설정된 자동 입찰이 하나 초과인 경우 두번째 최대 입찰가 + gap 가격으로 입찰가 설정
            if (activeAutoBids.size() > 1) {
                long secondMaxBidPrice = activeAutoBids.get(1).getMaxAutoBidPrice();
                nextBidPrice = Math.max(nextBidPrice, secondMaxBidPrice + 1);
            }

            Bid bid = Bid.create(topAutoBid.getAutoBidderId(), this.id, nextBidPrice);
            bids.add(bid);
            this.currentPrice = nextBidPrice;
            newBids.add(bid);

            autoBids.remove(topAutoBid);  // top 삭제
            autoBids = autoBids.stream()  // 나머지 비활성화
                    .map(AutoBid::deactivate)
                    .collect(Collectors.toCollection(ArrayList::new));
            autoBids.add(topAutoBid.updateCurrentAutoBidPrice(nextBidPrice));  // top 현재가만 수정해서 다시 넣기
        }

        return newBids;
    }

    /**
     * 특정 사용자의 자동 입찰 기능을 비활성화합니다.
     * 해당 사용자의 활성화된 자동 입찰이 있다면 비활성화 됩니다.
     *
     * @param userId 자동 입찰을 비활성화할 사용자의 고유 식별자
     */
    public void deactivateAutoBidByUserId(Long userId) {
        autoBids = autoBids.stream()
                .map(ab -> ab.getAutoBidderId().equals(userId) && ab.isActive()
                        ? ab.deactivate()
                        : ab)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 고유 식별자(autoBidId)로 식별된 자동 입찰을 비활성화합니다.
     * 자동 입찰이 활성화되어 있고 제공된 식별자와 일치하는 경우
     * 비활성화되고 자동 입찰 목록에 업데이트됩니다.
     *
     * @param autoBidId 비활성화할 자동 입찰의 고유 식별자
     */
    public void deactivateAutoBidByAutoBidId(UUID autoBidId) {
        autoBids = autoBids.stream()
                .map(ab -> ab.getId().equals(autoBidId) && ab.isActive()
                        ? ab.deactivate()
                        : ab)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}