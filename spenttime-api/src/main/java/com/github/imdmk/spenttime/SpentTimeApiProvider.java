package com.github.imdmk.spenttime;

public class SpentTimeApiProvider {

    private static SpentTimeApi SPENT_TIME_API;

    private SpentTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static SpentTimeApi get() {
        if (SPENT_TIME_API == null) {
            throw new IllegalStateException("The DiscordIntegrationAPI isn't registered.");
        }

        return SPENT_TIME_API;
    }

    static void register(SpentTimeApi spentTimeApi) {
        if (SPENT_TIME_API != null) {
            throw new IllegalStateException("The DiscordIntegrationAPI is already registered.");
        }

        SPENT_TIME_API = spentTimeApi;
    }

    static void unregister() {
        if (SPENT_TIME_API == null) {
            throw new IllegalStateException("The DiscordIntegrationAPI isn't registered.");
        }

        SPENT_TIME_API = null;
    }
}
