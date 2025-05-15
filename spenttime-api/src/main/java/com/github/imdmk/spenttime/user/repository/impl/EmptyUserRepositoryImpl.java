package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EmptyUserRepositoryImpl implements UserRepository {

    @Override
    public CompletableFuture<Optional<User>> findByUUID(@NotNull UUID uuid) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public Optional<User> findByNameDirect(@NotNull String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<User>> findTopUsersBySpentTime(long limit) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public CompletableFuture<User> save(@NotNull User user) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull User user) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public CompletableFuture<Void> resetGlobalSpentTime() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }
}
