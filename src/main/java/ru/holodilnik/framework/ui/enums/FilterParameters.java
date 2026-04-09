package ru.holodilnik.framework.ui.enums;

public enum FilterParameters {
    POPULAR("Популярные"),
    PRICE_LOW_TO_HIGH("Сначала дешевые"),
    PRICE_HIGH_TO_LOW("Сначала дорогие"),
    AVAILABILITY("По наличию на складе"),
    DISCOUNT("По размеру скидки"),
    RATING("По рейтингу");

    private final String displayName;

    FilterParameters(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
