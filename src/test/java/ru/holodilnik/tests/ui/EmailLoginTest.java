package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.core.config.ConfigLoader;
import ru.holodilnik.framework.services.email.EmailService;
import ru.holodilnik.framework.services.email.MailSlurpEmailService;
import ru.holodilnik.framework.ui.pages.AuthPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Авторизация")
@Feature("Вход по email-коду")
@Story("Пользователь получает код на почту и входит в систему")
@Owner("QA Team")
@Severity(SeverityLevel.BLOCKER)
@Tag("UI")
@Tag("auth")
public class EmailLoginTest extends BaseTest {

    private final AuthPage authPage = new AuthPage();
    private final EmailService emailService = new MailSlurpEmailService();

    @Test
    @DisplayName("Успешная авторизация через email-код")
    void userCanLoginByEmailCode() {
        String email = ConfigLoader.getTestEmail();

        authPage
                .open()
                .enterEmail(email)
                .clickGetCode();

        String code = emailService.waitForConfirmationCode();

        authPage
                .enterConfirmationCode(code)
                .submitCode()
                .checkHeaderVisible();
    }
}
