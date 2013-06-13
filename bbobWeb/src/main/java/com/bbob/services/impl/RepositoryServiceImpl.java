package com.bbob.services.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbob.models.BaseEntity;
import com.bbob.services.RepositoryService;

@Repository
@Transactional
public abstract class RepositoryServiceImpl<T extends BaseEntity> implements
        RepositoryService<T> {

    private static final Logger LOGGER = Logger
            .getLogger(RepositoryServiceImpl.class);

    private EntityManager entityManager;

    @PersistenceContext
    public final void setEntityManager(EntityManager entityManagerParam) {
        this.entityManager = entityManagerParam;
    }

    public final T get(Class<T> clazz, Long id) throws Exception {

        try {
            T t = entityManager.find(clazz, id);

            return t;

        } catch (IllegalArgumentException e) {
            LOGGER.error("error get: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error get: " + e.getMessage());
            throw new Exception(e);
        }

    }

    public final void create(T entity) throws Exception {
        try {
            entityManager.persist(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error create: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error create: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error create: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public final T update(T entity) throws Exception {
        try {
            return entityManager.merge(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error update: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error update: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error update: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public final void remove(Class<T> clazz, Long id)
            throws Exception {
        T t = get(clazz, id);
        try {
            entityManager.remove(t);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error remove by id: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error remove by id: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error remove by id: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public final void remove(T entity) throws Exception {
        try {
            entityManager.remove(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error remove: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error remove: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error remove: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public final void removeBy(Class<T> clazz, String fieldName,
            Object fieldValue) throws Exception {
        try {
            Query query = entityManager.createQuery(
                    "DELETE FROM " + clazz.getSimpleName()
                            + " item WHERE item." + fieldName
                            + " = :fieldValue").setParameter("fieldValue",
                    fieldValue);
            query.executeUpdate();
        } catch (IllegalArgumentException e) {
            LOGGER.error("error remove by: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error remove by: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error remove by: " + e.getMessage());
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public final List<T> findAll(Class<T> clazz) throws Exception {
        try {
            Query query = entityManager.createQuery("SELECT item FROM "
                    + clazz.getSimpleName() + " item");
            List<T> objList = (List<T>) query.getResultList();
            return objList;
        } catch (IllegalArgumentException e) {
            LOGGER.error("error findAll: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findAll: " + e.getMessage());
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public final List<T> findBy(Class<T> clazz, String fieldName,
            Object fieldValue) throws Exception {
        try {
            Query query = entityManager.createQuery(
                    "SELECT item FROM " + clazz.getSimpleName()
                            + " item WHERE item." + fieldName
                            + " = :fieldValue").setParameter("fieldValue",
                    fieldValue);
            List<T> objList = (List<T>) query.getResultList();
            return objList;
        } catch (IllegalArgumentException e) {
            LOGGER.error("error findBy: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findBy: " + e.getMessage());
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public final List<T> findBy(Class<T> clazz, Map<String, Object> properties)
            throws Exception {
        LOGGER.debug("Find by class called");

        try {
            if (properties != null && !properties.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryString.append("SELECT item FROM ");
                queryString.append(clazz.getSimpleName());
                queryString.append(" item WHERE ");
                for (String fieldName : properties.keySet()) {
                    queryString.append("item.");
                    queryString.append(fieldName);
                    queryString.append(" = :");
                    queryString.append(fieldName.replace(".", ""));
                    queryString.append(" and ");
                }
                queryString.delete(queryString.length() - 4,
                        queryString.length());
                Query query = entityManager.createQuery(queryString.toString());
                for (String fieldName : properties.keySet()) {
                    query.setParameter(fieldName.replace(".", ""),
                            properties.get(fieldName));
                }
                return query.getResultList();
            }

        } catch (IllegalArgumentException e) {
            LOGGER.error("error findBy properties: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findBy properties: " + e.getMessage());
            throw new Exception(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final T getFirst(Class<T> clazz) throws Exception {
        try {
            Query query = entityManager.createQuery("SELECT item FROM "
                    + clazz.getSimpleName() + " item");
            query.setMaxResults(1);
            List<T> objList = (List<T>) query.getResultList();
            if (objList.isEmpty()) {
                return null;
            }
            return objList.get(0);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error getFirst: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error getFirst: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public final void removeAll(Class<T> entityClass)
            throws Exception {
        try {
            Query query = entityManager.createQuery("DELETE FROM "
                    + entityClass.getSimpleName());
            query.executeUpdate();
        } catch (IllegalStateException e) {
            LOGGER.error("error removeAll: " + e.getMessage());
            throw new Exception(e);
        } catch (PersistenceException e) {
            LOGGER.error("error removeAll: " + e.getMessage());
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public final List<T> findByOrdered(Class<T> clazz, String fieldName,
            Object fieldValue, String orderBy) throws Exception {
        LOGGER.debug("Find by class called");

        try {
            Query query = entityManager.createQuery("SELECT item FROM "
                    + clazz.getSimpleName() + " item WHERE item." + fieldName
                    + " = :fieldValue ORDER BY " + orderBy);
            query.setParameter("fieldValue", fieldValue);
            List<T> objList = (List<T>) query.getResultList();
            return objList;
        } catch (IllegalArgumentException e) {
            LOGGER.error("error findByOrdered: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findByOrdered: " + e.getMessage());
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public final List<T> findByOrdered(Class<T> clazz,
            Map<String, Object> properties, String orderBy)
            throws Exception {
        LOGGER.debug("Find by class called");

        try {
            if (properties != null && !properties.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryString.append("SELECT item FROM ");
                queryString.append(clazz.getSimpleName());
                queryString.append(" item WHERE ");
                for (String fieldName : properties.keySet()) {
                    queryString.append("item.");
                    queryString.append(fieldName);
                    queryString.append(" = :");
                    queryString.append(fieldName.replace(".", ""));
                    queryString.append(" and ");
                }
                queryString.delete(queryString.length() - 4,
                        queryString.length());
                queryString.append(" ORDER BY " + orderBy);
                Query query = entityManager.createQuery(queryString.toString());
                for (String fieldName : properties.keySet()) {
                    query.setParameter(fieldName.replace(".", ""),
                            properties.get(fieldName));
                }
                return query.getResultList();
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("error findByOrdered: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findByOrdered: " + e.getMessage());
            throw new Exception(e);
        }

        return null;
    }

    public final List<T> findGroupBy(Class<T> clazz, List<String> attributes)
            throws Exception {
        LOGGER.debug("Find findGroupBy called");

        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT item FROM " + clazz.getSimpleName()
                    + " item GROUP BY ");
            for (String attr : attributes) {
                queryString.append(attr + ", ");
            }

            queryString.delete(queryString.length() - 2, queryString.length());

            Query query = entityManager.createQuery(queryString.toString());
            @SuppressWarnings("unchecked")
            List<T> objList = (List<T>) query.getResultList();
            return objList;
        } catch (IllegalArgumentException e) {
            LOGGER.error("error findGroupBy: " + e.getMessage());
            throw new Exception(e);
        } catch (IllegalStateException e) {
            LOGGER.error("error findGroupBy: " + e.getMessage());
            throw new Exception(e);
        }
    }
}
