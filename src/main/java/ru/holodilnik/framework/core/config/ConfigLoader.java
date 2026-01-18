package ru.holodilnik.framework.core.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Загружает config.properties один раз и даёт геттеры.
 */
public final class ConfigLoader {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) throw new IllegalStateException("config.properties не найден в classpath");
            PROPERTIES.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить config.properties", e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    public static String getEnv() {
        return get("env");
    }
}