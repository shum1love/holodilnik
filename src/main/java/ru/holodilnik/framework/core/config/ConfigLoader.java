package ru.holodilnik.framework.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Класс с управлением настройками проекта.
 */
public final class ConfigLoader {

    private static final Config CONFIG = ConfigFactory.load();

    private ConfigLoader() {}

    /** Окружение */
    public static String getEnv() {
        return CONFIG.getString("env");
    }

    /** Базовый URL */
    public static String getBaseUrl() {
        return CONFIG.getString("base-url");
    }

    /** Подсветка UI */
    public static boolean isUiHighlightEnabled() {
        return CONFIG.getBoolean("ui.highlight");
    }

    /** Скриншот при падении */
    public static boolean isScreenshotOnFail() {
        return CONFIG.getBoolean("ui.screenshot-on-fail");
    }

    /** Ретраи UI */
    public static int getUiActionRetryCount() {
        return CONFIG.getInt("ui.action-retry-count");
    }

    /** Таймаут Selenide */
    public static long getSelenideTimeout() {
        return CONFIG.getLong("selenide.timeout");
    }

    /** Таймаут загрузки страницы */
    public static long getPageLoadTimeout() {
        return CONFIG.getLong("selenide.page-load-timeout");
    }
}