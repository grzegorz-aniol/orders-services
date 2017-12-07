package org.gangel.common.services;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public interface EntityMapper<E extends AbstractEntity<? extends Serializable>, T> {

    T toDTO(E source);
    
    E toEntity(T source);
    
    default List<T> toDTO(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(d->toDTO(d)).collect(Collectors.toList());       
    }

    default Page<T> toDTO(Page<E> entitiesPage) {
        if (entitiesPage == null) {
            return null;
        }
        return entitiesPage.map((c) -> toDTO(c));      
    }

    default List<E> toEntity(List<T> transferList) {
        if (transferList == null) {
            return null;
        }
        return transferList.stream().map(r->toEntity(r)).collect(Collectors.toList());       
    }    
}
