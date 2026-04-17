package ru.holodilnik.framework.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс с управлением настройками проекта.
 */
public final class ConfigLoader {
    private static final Config CONFIG = buildConfig();

    private ConfigLoader() {
    }

    /** Возвращает окружение (env) */
    public static String getEnv() {
        return CONFIG.getString("env");
    }

    /** Возвращает базовый URL */
    public static String getBaseUrl() {
        if (CONFIG.hasPath("app.base-url")) {
            return CONFIG.getString("app.base-url");
        }

        final String environmentPath = String.format("environments.%s.base-url", getEnv());
        if (!CONFIG.hasPath(environmentPath)) {
            throw new IllegalStateException("Не найден base-url для env=" + getEnv());
        }

        return CONFIG.getString(environmentPath);
    }

    /** Включить подсветку UI-элементов */
    public static boolean isUiHighlightEnabled() {
        return CONFIG.getBoolean("ui.highlight");
    }

    /** Сделать скриншот при падении теста */
    public static boolean isScreenshotOnFail() {
        return CONFIG.getBoolean("ui.screenshot-on-fail");
    }

    /** Количество повторных попыток UI-действий */
    public static int getUiActionRetryCount() {
        return CONFIG.getInt("ui.action-retry-count");
    }

    /** Таймаут Selenide (мс) */
    public static long getSelenideTimeout() {
        return CONFIG.getLong("selenide.timeout");
    }

    /** Таймаут загрузки страницы Selenide (мс) */
    public static long getPageLoadTimeout() {
        return CONFIG.getLong("selenide.page-load-timeout");
    }

    /** Собирает итоговый конфиг */
    private static Config buildConfig() {
        return ConfigFactory.systemProperties()
                .withFallback(environmentOverrides())
                .withFallback(ConfigFactory.parseResourcesAnySyntax("application"))
                .resolve();
    }

    /** Создаёт слой конфига из переменных окружения */
    private static Config environmentOverrides() {
        final Map<String, Object> aliases = new LinkedHashMap<>();
        putIfPresent(aliases, "env", "ENV");
        putIfPresent(aliases, "app.base-url", "APP_BASE_URL", "BASE_URL");
        putIfPresent(aliases, "ui.highlight", "UI_HIGHLIGHT");
        putIfPresent(aliases, "ui.screenshot-on-fail", "UI_SCREENSHOT_ON_FAIL");
        putIfPresent(aliases, "ui.action-retry-count", "UI_ACTION_RETRY_COUNT");
        putIfPresent(aliases, "selenide.timeout", "SELENIDE_TIMEOUT");
        putIfPresent(aliases, "selenide.page-load-timeout", "SELENIDE_PAGE_LOAD_TIMEOUT");

        Config config = ConfigFactory.empty();
        for (final Map.Entry<String, Object> entry : aliases.entrySet()) {
            config = config.withValue(entry.getKey(), ConfigValueFactory.fromAnyRef(entry.getValue()));
        }

        return config;
    }

    /** Если найдена непустая env-переменная из списка, кладёт её значение в `aliases` по `configPath`. */
    private static void putIfPresent(final Map<String, Object> aliases, final String configPath, final String... envNames) {
        for (final String envName : envNames) {
            final String value = System.getenv(envName);
            if (value != null && !value.isBlank()) {
                aliases.put(configPath, value);
                return;
            }
        }
    }
}