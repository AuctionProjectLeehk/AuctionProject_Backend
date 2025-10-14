package com.leehk.auction.global;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 도메인 리스트와 엔티티 리스트를 동기화하는 헬퍼 클래스.
 *
 * <p>주로 JPA 환경에서 OneToMany 컬렉션을 동기화할 때 사용.</p>
 *
 * @param <E> Entity Type
 * @param <D> Domain Type
 * @param <ID> Identifier Type
 */
public class CollectionSyncHelper {

    /**
     * 엔티티 리스트를 도메인 리스트와 동기화합니다.
     *
     * @param entityList       기존 영속 엔티티 리스트 (변경 대상)
     * @param domainList       현재 도메인 상태 리스트
     * @param domainIdGetter   도메인 객체에서 ID 추출 함수
     * @param entityIdGetter   엔티티 객체에서 ID 추출 함수
     * @param updateFunc       기존 엔티티를 업데이트하는 함수 (entity, domain)
     * @param entityCreator    새 엔티티를 생성하는 함수 (domain → entity)
     * @param <E>              엔티티 타입
     * @param <D>              도메인 타입
     * @param <ID>             ID 타입
     */
    public static <E, D, ID> void sync(
            List<E> entityList,
            List<D> domainList,
            Function<D, ID> domainIdGetter,
            Function<E, ID> entityIdGetter,
            BiConsumer<E, D> updateFunc,
            Function<D, E> entityCreator
    ) {
        if (entityList == null || domainList == null)
            throw new IllegalArgumentException("entityList와 domainList는 null일 수 없습니다.");

        // 기존 엔티티를 Map<ID, Entity> 형태로 보관
        Map<ID, E> existingEntities = new HashMap<>();
        for (E entity : entityList) {
            ID id = entityIdGetter.apply(entity);
            if (id != null) existingEntities.put(id, entity);
        }

        List<E> newEntityList = new ArrayList<>();

        for (D domain : domainList) {
            ID id = domainIdGetter.apply(domain);

            if (id != null && existingEntities.containsKey(id)) {
                // 기존 엔티티 업데이트
                E existingEntity = existingEntities.remove(id);
                updateFunc.accept(existingEntity, domain);
                newEntityList.add(existingEntity);
            } else {
                // 신규 엔티티 생성
                E newEntity = entityCreator.apply(domain);
                newEntityList.add(newEntity);
            }
        }

        // 리스트 참조 유지 (JPA 컬렉션 동기화)
        entityList.clear();
        entityList.addAll(newEntityList);
    }
}
