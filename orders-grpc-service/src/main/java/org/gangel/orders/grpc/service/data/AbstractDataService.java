package org.gangel.orders.grpc.service.data;

import com.google.protobuf.Message;
import org.gangel.common.services.AbstractEntity;
import org.gangel.common.services.Pair;
import org.gangel.common.services.ServiceListener;
import org.gangel.orders.grpc.mappers.AbstractGrpcMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import javax.persistence.EntityNotFoundException;

public abstract class AbstractDataService<E extends AbstractEntity<ID>, 
        T extends Message, 
        B extends Message.Builder, 
        ID extends Serializable> 
    implements ServiceListener<E, ID>
    {

    protected abstract PagingAndSortingRepository<E, ID> getRepo();
    
    protected abstract AbstractGrpcMapper<E, T, B, ID> getMapper(); 
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public T getById(ID identifier) {
        E entity = getRepo().findOne(identifier);
        if (entity == null) {
            return null;
        }
        onGet(entity);
        T dto = (T) getMapper().toProto(entity).build();
        return dto;
    }
    
    @Transactional
    public ID save(T dto) {
        if (dto == null) {
            return null;
        }
        E entity = getMapper().toEntity(dto);
        beforeSave(entity);
        entity = getRepo().save(entity);
        afterSave(entity);
        return (entity != null ? entity.getId() : null);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public T saveAndGet(T dto) {
        if (dto == null) {
            return null;
        }
        E entity = getMapper().toEntity(dto);
        beforeSave(entity);
        entity = getRepo().save(entity);
        afterSave(entity);
        return (T) getMapper().toProto(entity).build();
    }
    
    @Transactional
    public void update(ID id, T dto) {
        if (dto == null) {
            return; 
        }
        if (id == null) {
            throw new RuntimeException("ID of object is not specified");
        }
        beforeUpdate(getRepo().findOne(id));
        E entity = getMapper().toEntity(dto);
        if (entity == null) {
            throw new EntityNotFoundException("Entity not found");
        }
        afterUpdate(entity);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public Page<T> getAll(Pageable pageable) {
        return getRepo().findAll(pageable).map(e -> (T)getMapper().toProto(e).build());
    }
    
    @Transactional(readOnly=true)
    public Pair<ID> getIdsRange() {
        ID id1 = null;
        ID id2 = null;
        Page<E> res1 = getRepo().findAll(new PageRequest(0, 1, Direction.ASC, "id" ));
        if (res1 != null && res1.getContent()!=null && res1.getContent().size() == 1) {
            id1 = res1.getContent().get(0).getId();            
        }
        Page<E> res2 = getRepo().findAll(new PageRequest(0, 1, Direction.DESC, "id" ));
        if (res2 != null && res2.getContent()!=null && res2.getContent().size() == 1) {
            id2 = res2.getContent().get(0).getId();
        }
        return new Pair<ID>(id1,id2);
    }
    
}
