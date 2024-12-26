package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserService {

    private final UserRepository userRepository;
    private final UserCache userCache;

    public UserService(UserRepository userRepository, UserCache userCache) {
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    public CompletableFuture<Optional<User>> getOrFindUser(UUID uuid) {
        Optional<User> cachedUser = this.userCache.get(uuid);

        if (cachedUser.isPresent()) {
            return CompletableFuture.completedFuture(cachedUser);
        }
        else {
            return this.userRepository.findByUUID(uuid);
        }
    }

    public Optional<User> getOrFindUser(String name) {
        return this.userCache.get(name).or(() -> this.userRepository.findByName(name));
    }
}
