package ru.holodilnik.framework.services.email;

/**
 * Контракт для получения кода подтверждения из email.
 */
public interface EmailService {

    /**
     * Дожидается нового письма и возвращает код подтверждения.
     */
    String waitForConfirmationCode();
}
