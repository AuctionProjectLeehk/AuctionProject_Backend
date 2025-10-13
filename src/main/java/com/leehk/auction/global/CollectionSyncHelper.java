package com.leehk.auction.global;

import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CollectionSyncHelper {

    /**
     * 엔티티 리스트와 도메인 리스트를 동기화
     * @param entityList 영속성 관리되는 엔티티 리스트
     * @param domainList 현재 도메인 상태 리스트
     * @param idGetter 도메인에서 ID 가져오는 함수
     * @param updateFunc 존재하는 엔티티 업데이트 함수 (entity, domain)
     * @param entitySupplier 새 엔티티 생성 함수 (domain -> entity)
     * @param <E> 엔티티 타입
     * @param <D> 도메인 타입
     * @param <ID> ID 타입
     */
    public static <E, D, ID> void sync(
            List<E> entityList,
            List<D> domainList,
            Function<D, ID> idGetter,
            BiConsumer<E, D> updateFunc,
            Function<D, E> entitySupplier
    ) {
        Map<ID, E> existingMap = new HashMap<>();
        for (E entity: entityList) {
            ID id = (ID) getIdFromEntity(entity);  // entity에서 ID 꺼내는 방법은 엔티티마다 맞춰야 함
            if (id != null) existingMap.put(id, entity);
        }

        List<E> updatedEntities = new ArrayList<>();
        for (D domain: domainList) {
            ID id = idGetter.apply(domain);
            if (id != null && existingMap.containsKey(id)) {
                E entity = existingMap.remove(id);
                updateFunc.accept(entity, domain);
                updatedEntities.add(entity);
            } else {
                updatedEntities.add(entitySupplier.apply(domain));
            }
        }

        entityList.clear();
        entityList.addAll(updatedEntities);
    }

    private static <E, ID> ID getIdFromEntity(E entity) {
        try {
            return (ID) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }
}
