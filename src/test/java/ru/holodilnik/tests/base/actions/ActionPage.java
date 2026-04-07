package ru.holodilnik.tests.base.actions;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

import java.util.ArrayList;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Интерфейс с обёртками над типовыми browser-actions Selenide.
 */
public interface ActionPage {

    @Step("Обновить текущую страницу")
    default void refreshPage() {
        Selenide.refresh();
    }

    @Step("Перейти назад в браузере")
    default void backPage() {
        Selenide.back();
    }

    @Step("Перейти вперёд в браузере")
    default void forwardPage() {
        Selenide.forward();
    }

    @Step("Открыть новую вкладку: {url}")
    default void openNewTab(String url) {
        Selenide.executeJavaScript("window.open(arguments[0], '_blank');", url);
        switchToLastTab();
    }

    @Step("Переключиться на вкладку с индексом {index}")
    default void switchToTab(int index) {
        var handles = new ArrayList<>(getWebDriver().getWindowHandles());
        if (index < 0 || index >= handles.size()) {
            throw new IllegalArgumentException("Некорректный индекс вкладки: " + index + ", вкладок всего: " + handles.size());
        }
        getWebDriver().switchTo().window(handles.get(index));
    }

    @Step("Переключиться на последнюю вкладку")
    default void switchToLastTab() {
        var handles = new ArrayList<>(getWebDriver().getWindowHandles());
        getWebDriver().switchTo().window(handles.get(handles.size() - 1));
    }

    @Step("Закрыть текущую вкладку")
    default void closeCurrentTab() {
        getWebDriver().close();
        switchToLastTab();
    }
}
