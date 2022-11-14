package ru.kharpukhaev.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kharpukhaev.model.Advertisement;
import ru.kharpukhaev.model.Filter;

import java.util.List;

public interface AdvertisementRepository extends CrudRepository<Advertisement, Long> {
    List<Advertisement> findAllByAvitoId (Long adId);
    List<Advertisement> findAllByFilterId (Long filterId);

    List<Advertisement> findTop5ByFilterId (Long id);
}
