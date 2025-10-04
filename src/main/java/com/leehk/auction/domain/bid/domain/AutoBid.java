package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AutoBid {

    private final UUID id;
    private final Long autoBidderId;
    private final Long auctionId;
    private final long maxAutoBidPrice;
    private final long currentAutoBidPrice;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private AutoBid(UUID id, Long autoBidderId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.autoBidderId = autoBidderId;
        this.auctionId = auctionId;
        this.maxAutoBidPrice = maxAutoBidPrice;
        this.currentAutoBidPrice = currentAutoBidPrice;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 제공된 매개변수를 사용하여 새로운 자동 입찰 인스턴스를 생성하고 반환합니다.
     * 현재 자동 입찰가가 최대 자동 입찰가를 초과하지 않는지 검증합니다.
     *
     * @param userId              자동 입찰을 하는 사용자의 ID
     * @param auctionId           자동 입찰이 이루어지는 경매의 ID
     * @param maxAutoBidPrice     사용자가 설정한 최대 자동 입찰가
     * @param currentAutoBidPrice 현재 자동 입찰가
     * @return 새로운 자동 입찰 인스턴스
     * @throws CustomException 현재 자동 입찰가가 최대 자동 입찰가를 초과하는 경우
     */
    public static AutoBid create(Long userId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice) {
        if (currentAutoBidPrice > maxAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE);
        }

        LocalDateTime now = LocalDateTime.now();
        return new AutoBid(UUID.randomUUID(),
                userId,
                auctionId,
                maxAutoBidPrice,
                currentAutoBidPrice,
                true,
                now,
                now
        );
    }

    /**
     * 기존의 자동 입찰 객체를 복원합니다.
     *
     * @param id                  자동 입찰의 고유 식별자
     * @param autoBidderId        자동 입찰과 연관된 사용자의 ID
     * @param auctionId           자동 입찰이 관련된 경매의 ID
     * @param maxAutoBidPrice     사용자가 설정한 최대 입찰가
     * @param currentAutoBidPrice 현재 입찰가
     * @param active              자동 입찰의 활성화 상태
     * @param createdAt           자동 입찰 생성 시간
     * @param updatedAt           자동 입찰 마지막 수정 시간
     * @return 주어진 매개변수로 복원된 자동 입찰 인스턴스
     */
    public static AutoBid restore(UUID id, Long autoBidderId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AutoBid(id, autoBidderId, auctionId, maxAutoBidPrice, currentAutoBidPrice, active, createdAt, updatedAt);
    }

    /**
     * 현재 자동 입찰가를 제공된 값으로 업데이트합니다. 새로운 현재 자동 입찰가가
     * 최대 자동 입찰가를 초과하지 않는지 검증합니다. 검증에 실패하면
     * {@link CustomException}이 발생합니다.
     *
     * @param newCurrentAutoBidPrice 설정할 새로운 현재 자동 입찰가
     * @return 현재 자동 입찰가가 업데이트된 새로운 {@code AutoBid} 인스턴스
     * @throws CustomException 새로운 현재 자동 입찰가가 최대 자동 입찰가를 초과하는 경우
     */
    public AutoBid updateCurrentAutoBidPrice(Long newCurrentAutoBidPrice) {
        if (newCurrentAutoBidPrice > this.maxAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE);
        }

        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                this.maxAutoBidPrice, newCurrentAutoBidPrice,
                this.active, this.createdAt, LocalDateTime.now());
    }

    /**
     * 기존 자동 입찰 인스턴스의 최대 입찰가를 업데이트합니다. 새로운 최대 입찰가는
     * 현재 자동 입찰가보다 크거나 같아야 합니다. 이 조건이 충족되지 않으면
     * {@code CustomException}이 발생합니다.
     * 또한 'active' 상태를 true로 설정하여 자동 입찰이 활성화되도록 합니다.
     *
     * @param newMaxPrice 설정할 새로운 최대 자동 입찰가
     * @return 최대 자동 입찰가가 업데이트된 새로운 {@code AutoBid} 인스턴스
     * @throws CustomException 새로운 최대 입찰가가 현재 자동 입찰가보다 낮은 경우
     */
    public AutoBid updateMaxAutoBidPrice(long newMaxPrice) {
        if (newMaxPrice < this.currentAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_MAX_PRICE);
        }

        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                newMaxPrice, this.currentAutoBidPrice,
                true, this.createdAt, LocalDateTime.now());
    }

    /**
     * 현재 자동 입찰 인스턴스를 비활성화합니다. 동일한 속성을 가지지만
     * 'active' 상태가 false로 설정된 새로운 인스턴스를 생성합니다.
     *
     * @return 'active' 상태가 false로 설정된 새로운 자동 입찰 인스턴스
     */
    public AutoBid deactivate() {
        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                this.maxAutoBidPrice, this.currentAutoBidPrice,
                false, this.createdAt, LocalDateTime.now());
    }
}