package ru.kharpukhaev.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kharpukhaev.model.Mark;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    Mark findMarkByName(String name);
    List<Mark> findAll();
}
