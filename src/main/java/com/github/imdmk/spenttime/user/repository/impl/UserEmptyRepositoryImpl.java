package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserEmptyRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findByUUID(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<User> findUsersByOrderSpentTime(long limit) {
        return Collections.emptyList();
    }

    @Override
    public void save(User user) {
    }

    @Override
    public void delete(UUID uuid) {
    }
}
