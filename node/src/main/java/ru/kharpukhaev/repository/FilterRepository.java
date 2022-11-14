package ru.kharpukhaev.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kharpukhaev.model.Filter;

import java.util.List;

public interface FilterRepository extends CrudRepository<Filter, Long> {
    Filter findByTelegramId (Long id);
    List<Filter> findAllByTelegramId (Long id);
    Filter findByTelegramIdAndColorIsNull(Long id);
    Filter findFirstById(Long id);
    List<Filter> findAllByUpdatesIsTrue();
}
