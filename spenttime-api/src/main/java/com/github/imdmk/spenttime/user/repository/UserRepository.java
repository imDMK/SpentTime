package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    CompletableFuture<Optional<User>> findByUUID(UUID uuid);

    CompletableFuture<Optional<User>> findByName(String name);

    Optional<User> findByNameDirect(String name);

    CompletableFuture<List<User>> findTopUsersBySpentTime(long limit);

    CompletableFuture<User> save(User user);

    CompletableFuture<Void> delete(User user);

    CompletableFuture<Void> resetGlobalSpentTime();
}
