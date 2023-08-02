package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.UserEmptyRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserManagerTest {

    private static final UserRepository USER_EMPTY_REPOSITORY = new UserEmptyRepositoryImpl();

    @Test
    void testCreateUser() {
        UserManager userManager = new UserManager(USER_EMPTY_REPOSITORY);

        UUID exceptedUuid = UUID.randomUUID();
        String exceptedName = "DMK";

        User user = userManager.createUser(exceptedUuid, exceptedName);

        assertEquals(exceptedUuid, user.getUuid());
        assertEquals(exceptedName, user.getName());
    }

    @Test
    void testGetUser() {
        UserManager userManager = new UserManager(USER_EMPTY_REPOSITORY);

        userManager.createUser(UUID.randomUUID(), "imDMK");
        userManager.createUser(UUID.randomUUID(), "DMK");

        assertTrue(userManager.getUser("DMK").isPresent());
        assertTrue(userManager.getUser("imDMK").isPresent());

        assertTrue(userManager.getUser("UNKNOWN_USER").isEmpty());
    }

    @Test
    void testSizeUsersCache() {
        UserManager userManager = new UserManager(USER_EMPTY_REPOSITORY);

        userManager.createUser(UUID.randomUUID(), "imDMK");
        userManager.createUser(UUID.randomUUID(), "DMK");
        userManager.createUser(UUID.randomUUID(), "SpentTimeUser");

        assertEquals(3, userManager.getNameUserCache().size());
    }
}


