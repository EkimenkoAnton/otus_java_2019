package ru.otus.repository;

import ru.otus.domain.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    long create(User name);
}
