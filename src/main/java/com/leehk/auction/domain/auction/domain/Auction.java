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

        AutoBid autoBid = AutoBid.create(userId, this.id, maxAutoBidPrice);
        autoBids.add(autoBid);
        return autoBid;
    }

    /**
     * 활성화된 자동 입찰들을 실행합니다.
     * 각각의 활성화된 자동 입찰은 현재 입찰가 기준 내림차순으로 처리됩니다.
     * 최대 자동 입찰가가 허용하는 한 최소 단위로 입찰가를 증가시키며 입찰을 진행합니다.
     * 더 이상 진행 가능한 자동 입찰이 없을 때까지 반복합니다.
     *
     * @return 자동 입찰 실행으로 생성된 새로운 입찰 목록
     */
    public List<Bid> executeAutoBids() {
        boolean bidPlaced;
        List<Bid> newBids = new ArrayList<>();

        do {
            bidPlaced = false;

            for (AutoBid autoBid : getActiveAutoBidsSorted()) {
                long nextBidPrice = this.currentPrice + 1; // 최소 단위로 입찰가 증가

                if (autoBid.getCurrentAutoBidPrice() >= nextBidPrice) {
                    Bid bid = Bid.create(autoBid.getAutoBidderId(), this.id, nextBidPrice);
                    bids.add(bid);
                    this.currentPrice = nextBidPrice;

                    // 자동 입찰의 현재 입찰가 갱신
                    autoBids.remove(autoBid);
                    autoBids.add(autoBid.updateCurrentAutoBidPrice(nextBidPrice));

                    newBids.add(bid);
                    bidPlaced = true;
                    break;
                }
            }
        } while (bidPlaced);

        return newBids;
    }

    // 활성화된 자동 입찰 목록을 현재 입찰가 기준 내림차순 정렬
    private List<AutoBid> getActiveAutoBidsSorted() {
        List<AutoBid> activeAutoBids = new ArrayList<>();

        for (AutoBid autoBid : autoBids) {
            if (autoBid.isActive()) {
                activeAutoBids.add(autoBid);
            }
        }

        activeAutoBids.sort(Comparator.comparingLong(AutoBid::getCurrentAutoBidPrice).reversed());
        return activeAutoBids;
    }

    /**
     * 특정 사용자의 자동 입찰을 비활성화합니다.
     * 해당 사용자 ID와 연결된 활성화된 자동 입찰을 찾아 비활성화 처리하고,
     * 비활성화된 버전으로 자동 입찰 목록을 갱신합니다.
     *
     * @param userId 자동 입찰을 비활성화할 사용자 ID
     */
    public void deactivateAutoBid(Long userId) {
        autoBids.stream()
                .filter(ab -> ab.getAutoBidderId().equals(userId) && ab.isActive())
                .forEach(ab -> {
                    autoBids.remove(ab);
                    autoBids.add(ab.deactivate());
                });
    }
}