package org.example.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> getItems(int offset, int limit);

    List<T> getAll();

    int getTotalCount();

    Optional<T> getById(Integer id);

    T save(T entity);

    T update(T entity);

    void delete(T entity);
}