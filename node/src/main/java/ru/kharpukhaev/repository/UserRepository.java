package ru.kharpukhaev.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kharpukhaev.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByTelegramId(Long id);
}
