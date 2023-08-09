package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByUUID(UUID uuid);

    Optional<User> findByName(String name);

    List<User> findUsersByOrderSpentTime(long limit);

    void save(User user);

    void delete(UUID uuid);

    void resetGlobalSpentTime();
}
