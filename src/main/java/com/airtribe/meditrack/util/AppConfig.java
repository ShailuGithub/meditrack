package com.airtribe.meditrack.util;

/**
 * Singleton application configuration — <b>lazy</b> initialization variant,
 * using double-checked locking so the instance is only built the first time
 * {@link #getInstance()} is actually called, and concurrent first-callers
 * still only ever construct one instance. Compare with {@link IdGenerator},
 * which is an eager singleton.
 *
 * <p>The static block runs once, when the class is first loaded by the
 * JVM's class loader — independent of whether/when {@code getInstance()}
 * is ever invoked.
 */
public final class AppConfig {

    private static volatile AppConfig instance;

    static {
        System.out.println("[AppConfig] static block: MediTrack class metadata loading...");
    }

    private final String appName;
    private final String version;

    private AppConfig() {
        this.appName = "MediTrack";
        this.version = "1.0.0";
    }

    public static AppConfig getInstance() {
        AppConfig result = instance;
        if (result == null) {
            synchronized (AppConfig.class) {
                result = instance;
                if (result == null) {
                    instance = result = new AppConfig();
                }
            }
        }
        return result;
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }
}
