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
        Configuration.timeout = 12000;
        Configuration.pageLoadTimeout = 30000;

        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }
}
