package ru.holodilnik.tests.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import ru.holodilnik.framework.core.config.ConfigLoader;

public abstract class BaseTest {

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = ConfigLoader.getBaseUrl();
        // Configuration.browser = "chrome";  // уже в selenide.properties
        // Configuration.headless = true;     // переопредели в профиле если нужно

        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }
}
