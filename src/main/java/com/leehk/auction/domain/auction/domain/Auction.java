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

    /**
     * 입찰자 ID와 입찰가로 새로운 입찰을 등록합니다.
     * 입찰의 유효성을 검사하고 경매의 현재가를 업데이트합니다.
     * 새로운 Bid 객체가 생성되어 경매의 입찰 목록에 추가됩니다.
     *
     * @param bidderId 입찰을 하는 입찰자의 ID
     * @param bidPrice 입찰 가격
     * @return 입찰 정보가 담긴 새로운 Bid 객체
     * @throws CustomException 경매가 종료되었거나 입찰가가 너무 낮은 경우와 같은 유효하지 않은 입찰일 때
     */
    public Bid placeBid(Long bidderId, long bidPrice) {
        // 소유자인지 확인
        if (bidderId.equals(owner.getId())) {
            throw new CustomException(ErrorCode.OWNER_CANNOT_BID);
        }
        
        // 경매 진행중 확인
        if (status != AuctionStatus.ONGOING) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        // 입찰가 검증
        if (bidPrice <= currentPrice) {
            throw new CustomException(ErrorCode.BID_TOO_LOW);
        }

        this.currentPrice = bidPrice;
        Bid bid = Bid.create(bidderId, this.id, bidPrice);

        bids.add(bid);
        return bid;
    }

    /**
     * 경매의 입찰을 취소합니다. 입찰자 ID가 원래 입찰자와 일치하는 경우에만 취소가 가능합니다.
     * 경매의 현재가는 남은 입찰 중 최고가 또는 입찰이 없는 경우 시작가로 업데이트됩니다.
     *
     * @param bidId    취소할 입찰의 고유 식별자
     * @param bidderId 입찰을 취소하려는 입찰자의 ID
     * @throws CustomException 입찰을 찾을 수 없거나 입찰자가 취소 권한이 없는 경우
     */
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

    /**
     * 입찰 목록에서 최고 입찰자의 ID를 조회합니다.
     * 최고 입찰자는 최고 입찰가를 기준으로 결정됩니다.
     * 입찰이 없는 경우 null을 반환합니다.
     *
     * @return 최고 입찰자의 ID 또는 입찰이 없는 경우 null
     */
    public Long getHighestBidderId() {
        return bids.stream()
                .max(Comparator.comparingLong(Bid::getBidPrice))
                .map(Bid::getBidderId)
                .orElse(null);
    }

    /**
     * 경매에 등록된 모든 입찰 목록을 조회합니다.
     *
     * @return 경매와 관련된 모든 입찰을 포함하는 목록
     */
    public List<Bid> getBids() {
        return new ArrayList<>(bids);
    }

    /**
     * 경매의 상태를 ENDED로 변경하여 종료합니다.
     * 이미 종료된 경매인 경우 예외가 발생합니다.
     *
     * @throws CustomException 경매 상태가 이미 ENDED인 경우
     */
    public void endAuction() {
        if (status == AuctionStatus.ENDED) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        this.status = AuctionStatus.ENDED;
    }

    /**
     * 경매의 제목, 설명, 시작가, 시작 시간, 종료 시간을 포함한 상세 정보를 업데이트합니다.
     * 이미 종료된 경매인 경우 예외가 발생합니다.
     *
     * @param title       경매의 새로운 제목
     * @param description 경매의 새로운 설명
     * @param startPrice  경매의 새로운 시작가
     * @param startTime   경매의 새로운 시작 시간
     * @param endTime     경매의 새로운 종료 시간
     * @throws CustomException 경매 상태가 이미 ENDED인 경우
     */
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

    /**
     * 경매의 현재가를 지정된 새로운 가격으로 업데이트합니다.
     *
     * @param newPrice 경매의 새로운 가격. 양수여야 합니다.
     */
    public void updateAuctionPrice(long newPrice) {
        this.currentPrice = newPrice;
    }

    /**
     * 경매에서 가장 높은 입찰을 조회합니다.
     * 최고 입찰은 입찰가를 기준으로 결정됩니다.
     * 입찰이 없는 경우 예외가 발생합니다.
     *
     * @return 경매의 최고 입찰을 나타내는 Bid 객체
     * @throws CustomException 입찰을 찾을 수 없는 경우
     */
    public Bid getHighestBid() {
        return bids.stream()
                .max(Comparator.comparingLong(Bid::getBidPrice))
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));
    }

    /**
     * 진행 중인 경매에서 사용자를 위한 자동 입찰을 등록하거나 업데이트합니다.
     * 해당 사용자의 자동 입찰이 이미 등록되어 있다면 최대 자동 입찰가가 업데이트되고,
     * 그렇지 않은 경우 새로운 자동 입찰이 생성됩니다.
     *
     * @param userId          자동 입찰을 등록하는 사용자의 ID
     * @param maxAutoBidPrice 사용자가 자동으로 입찰할 최대 가격
     * @return 등록되거나 업데이트된 AutoBid 인스턴스
     * @throws CustomException 경매가 진행 중이 아니거나 입력된 최대 자동 입찰가가 현재 경매가보다
     *                         낮거나 같은 경우 발생
     */
    public AutoBid registerAutoBid(Long userId, long maxAutoBidPrice) {
        // 소유자인지 확인
        if (userId.equals(owner.getId())) {
            throw new CustomException(ErrorCode.OWNER_CANNOT_AUTO_BID);
        }

        // 경매 진행중 확인
        if (status != AuctionStatus.ONGOING) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

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
     * 진행 중인 경매의 자동 입찰 프로세스를 실행합니다. 활성화된 자동 입찰을 평가하고,
     * 최고 및 두 번째로 높은 자동 입찰 최대가를 기준으로 다음 입찰가를 결정하며,
     * 낙찰된 입찰을 입찰 목록에 추가합니다. 입찰이 이루어질 때마다 경매의 현재가가
     * 업데이트됩니다. 더 이상 유효한 자동 입찰을 처리할 수 없을 때까지 계속됩니다.
     *
     * @return 자동 입찰 프로세스 중에 새로 생성된 입찰 목록
     * @throws CustomException 경매가 진행 중이 아닌 경우 발생
     */
    public List<Bid> executeAutoBids() {
        if (status != AuctionStatus.ONGOING) {
            throw new CustomException(ErrorCode.AUCTION_ALREADY_ENDED);
        }

        int minGap = 1; // 최소 입찰 단위
        List<Bid> newBids = new ArrayList<>();

        // 활성화된 자동 입찰을 최대 입찰가 기준 내림차순, 업데이트 순서 기준 정렬
        List<AutoBid> activeAutoBids = autoBids.stream()
                .filter(AutoBid::isActive)
                .sorted(Comparator
                        .comparingLong(AutoBid::getMaxAutoBidPrice).reversed()
                        .thenComparing(AutoBid::getUpdatedAt)
                )
                .toList();

        // 활성화된 자동 입찰이 없으면 종료
        if (activeAutoBids.isEmpty()) return newBids;

        AutoBid topAutoBid = activeAutoBids.get(0);
        long nextBidPrice = this.currentPrice + minGap;

        // 활성화된 자동 입찰의 최대가가 현재가보다 높지 않으면 종료
        if (topAutoBid.getMaxAutoBidPrice() < nextBidPrice) return newBids;

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

        return newBids;
    }

    /**
     * 주어진 사용자 ID에 연결된 자동 입찰을 비활성화합니다.
     * 해당 사용자의 활성화된 자동 입찰을 찾아 비활성화하고
     * 자동 입찰 목록을 갱신합니다. 해당 사용자의 활성화된 자동 입찰을 찾지 못하면
     * CustomException이 발생합니다.
     *
     * @param userId 자동 입찰을 비활성화할 사용자의 고유 식별자
     */
    public void deactivateAutoBidByUserId(Long userId) {
        AutoBid deactivatedAutoBid = autoBids.stream()
                .filter(ab -> ab.getAutoBidderId().equals(userId) && ab.isActive())
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND))
                .deactivate();

        autoBids.removeIf(ab -> ab.getId().equals(deactivatedAutoBid.getId()));
        autoBids.add(deactivatedAutoBid);
    }

    /**
     * 주어진 자동 입찰 ID로 식별되는 자동 입찰을 비활성화합니다.
     * 해당 ID를 가진 활성화된 자동 입찰이 존재하면 비활성화하고
     * 자동 입찰 목록을 갱신합니다. 활성화된 자동 입찰을 찾지 못하면
     * CustomException이 발생합니다.
     *
     * @param autoBidId 비활성화할 자동 입찰의 고유 식별자
     */
    public void deactivateAutoBidByAutoBidId(UUID autoBidId) {
        AutoBid deactivatedAutoBid = autoBids.stream()
                .filter(ab -> ab.getId().equals(autoBidId) && ab.isActive())
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND))
                .deactivate();

        autoBids.removeIf(ab -> ab.getId().equals(deactivatedAutoBid.getId()));
        autoBids.add(deactivatedAutoBid);
    }
}