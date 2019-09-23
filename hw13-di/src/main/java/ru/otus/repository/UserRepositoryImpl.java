package ru.otus.repository;

import org.springframework.stereotype.Repository;
import ru.otus.domain.Address;
import ru.otus.domain.Phone;
import ru.otus.domain.User;
import ru.otus.services.dbservice.DBService;
import ru.otus.services.dbservicemanager.DBServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DBService dbService;

    public UserRepositoryImpl(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public List<User> findAll() {
        return dbService.loadEntities(User.class);
    }

    @Override
    public long create(User user) {
        dbService.create(user);
        return user.getId();
    }

}
