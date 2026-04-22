package ru.holodilnik.tests.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.holodilnik.framework.core.config.ConfigLoader;
import ru.holodilnik.framework.ui.pages.ActionPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseTest implements ActionPage {
    private static boolean allureListenerRegistered;

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = ConfigLoader.getBaseUrl();
        Configuration.timeout = ConfigLoader.getSelenideTimeout();
        Configuration.pageLoadTimeout = ConfigLoader.getPageLoadTimeout();

        if (!allureListenerRegistered) {
            SelenideLogger.addListener("allure", new AllureSelenide()
                    .screenshots(true)
                    .savePageSource(true)
                    .includeSelenideSteps(false));
            allureListenerRegistered = true;
        }
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }
}
