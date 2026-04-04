package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Локаторы страницы авторизации (пример).
 */
public final class AuthPageLocators {

    private AuthPageLocators() {
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    public static UiElement emailInput() {
        return ui("Поле email", $("input[type='email']"));
    }

    public static UiElement getCodeButton() {
        return ui("Кнопка 'Получить код'", $x("//button[contains(.,'Получить код')]"));
    }

    public static UiElement codeInput() {
        return ui("Поле кода", $("input[inputmode='numeric']"));
    }

    public static UiElement submitCodeButton() {
        return ui("Кнопка подтверждения кода", $x("//button[contains(.,'Войти') or contains(.,'Подтвердить')]"));
    }
}
