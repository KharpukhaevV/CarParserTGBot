package ru.kharpukhaev.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kharpukhaev.model.Model;

import java.util.List;

public interface ModelRepository extends CrudRepository<Model, Long> {
    List<Model> findAllByMark (long id);
}
