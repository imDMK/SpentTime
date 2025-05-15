package com.github.imdmk.spenttime;

import org.jetbrains.annotations.NotNull;

/**
 * Static access point for the {@link SpentTimeApi}.
 * Acts as a global registry for the current instance.
 * <p>
 * Not thread-safe.
 */
public class SpentTimeApiProvider {

    private static SpentTimeApi SPENT_TIME_API;

    private SpentTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Returns the registered {@link SpentTimeApi}.
     *
     * @return the registered API
     * @throws IllegalStateException if the API is not yet registered
     */
    public synchronized static SpentTimeApi get() {
        if (SPENT_TIME_API == null) {
            throw new IllegalStateException("The SpentTimeApi isn't registered.");
        }

        return SPENT_TIME_API;
    }

    /**
     * Registers the {@link SpentTimeApi} instance.
     *
     * @param spentTimeApi the API instance to register
     * @throws IllegalStateException if already registered
     */
    static synchronized void register(@NotNull SpentTimeApi spentTimeApi) {
        if (SPENT_TIME_API != null) {
            throw new IllegalStateException("The SpentTimeApi is already registered.");
        }

        SPENT_TIME_API = spentTimeApi;
    }

    /**
     * Forces to register the {@link SpentTimeApi} instance.
     */
    static void forceRegister(@NotNull SpentTimeApi api) {
        SPENT_TIME_API = api;
    }

    /**
     * Unregisters the {@link SpentTimeApi}.
     *
     * @throws IllegalStateException if no API was registered
     */
    static synchronized void unregister() {
        if (SPENT_TIME_API == null) {
            throw new IllegalStateException("The SpentTimeApi isn't registered.");
        }

        SPENT_TIME_API = null;
    }
}
