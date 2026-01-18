package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Главная страница holodilnik.ru
 */
public class MainPage extends BasePage {

    private final SelenideElement headerLogo = $x("//span[@class='site-header__logo-brand']");
    private final SelenideElement searchInput = $x("//input[@id='top_search']");

    public MainPage() {
        super("/");
    }

    @Override
    public void isOpened() {
        headerLogo.shouldBe(visible.because("Логотип в шапке должен быть виден"));
    }

    public void search(String query) {
        searchInput.setValue(query).pressEnter();
    }
}