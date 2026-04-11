package ru.holodilnik.tests.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.holodilnik.framework.core.config.ConfigLoader;
import ru.holodilnik.tests.base.actions.ActionPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseTest implements ActionPage {

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = ConfigLoader.getBaseUrl();
        Configuration.timeout = 12000;
        Configuration.pageLoadTimeout = 30000;

        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(false));
    }


    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }
}
