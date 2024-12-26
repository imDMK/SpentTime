package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    CompletableFuture<Optional<User>> findByUUID(UUID uuid);

    Optional<User> findByName(String name);

    CompletableFuture<List<User>> findByOrderSpentTime(long limit);

    CompletableFuture<User> save(User user);

    CompletableFuture<Void> delete(UUID uuid);

    CompletableFuture<Void> resetGlobalSpentTime();
}