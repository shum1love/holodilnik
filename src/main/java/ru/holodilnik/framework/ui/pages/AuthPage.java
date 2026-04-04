package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.AuthPageLocators;

/**
 * Страница входа по email-коду.
 */
public final class AuthPage extends BasePage<AuthPage> {

    public AuthPage() {
        super("/login");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return AuthPageLocators.emailInput().getSelenideElement();
    }

    @Step("Ввести email: {email}")
    public AuthPage enterEmail(String email) {
        AuthPageLocators.emailInput().clearAndType(email);
        return this;
    }

    @Step("Нажать кнопку 'Получить код'")
    public AuthPage clickGetCode() {
        AuthPageLocators.getCodeButton().click();
        return this;
    }

    @Step("Ввести код подтверждения")
    public AuthPage enterConfirmationCode(String code) {
        AuthPageLocators.codeInput().clearAndType(code);
        return this;
    }

    @Step("Подтвердить код")
    public MainPage submitCode() {
        AuthPageLocators.submitCodeButton().click();
        return new MainPage().shouldBeOpen();
    }
}
