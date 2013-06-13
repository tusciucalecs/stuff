package com.bbob.services;

import java.util.List;
import java.util.Map;

import com.bbob.models.BaseEntity;

public interface RepositoryService<T extends BaseEntity> {

    T get(Class<T> clazz, Long id) throws Exception;

    void create(T entity) throws Exception;

    T update(T entity) throws Exception;

    void remove(Class<T> clazz, Long id) throws Exception;

    void remove(T entity) throws Exception;

    void removeBy(Class<T> clazz, String fieldName, Object fieldValue)
            throws Exception;

    List<T> findAll(Class<T> clazz) throws Exception;

    List<T> findBy(Class<T> clazz, String fieldName, Object fieldValue)
            throws Exception;

    List<T> findBy(Class<T> clazz, Map<String, Object> properties)
            throws Exception;

    T getFirst(Class<T> clazz) throws Exception;

    void removeAll(Class<T> entityClass) throws Exception;

    List<T> findByOrdered(Class<T> clazz, String fieldName, Object fieldValue,
            String orderBy) throws Exception;

    List<T> findGroupBy(Class<T> clazz, List<String> attributes)
            throws Exception;
}
