package ru.holodilnik.framework.services.email;

import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.holodilnik.framework.core.config.ConfigLoader;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Реализация EmailService через MailSlurp API.
 */
public final class MailSlurpEmailService implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailSlurpEmailService.class);
    private static final Pattern CODE_PATTERN = Pattern.compile("\\b(\\d{6})\\b");
    private static final long TIMEOUT_MS = 30_000L;

    private final WaitForControllerApi waitForControllerApi;
    private final UUID inboxId;

    public MailSlurpEmailService() {
        this(ConfigLoader.getMailSlurpApiKey(), ConfigLoader.getMailSlurpInboxId());
    }

    public MailSlurpEmailService(String apiKey, String inboxId) {
        ApiClient apiClient = Configuration.getDefaultApiClient();
        apiClient.setApiKey(apiKey);

        this.waitForControllerApi = new WaitForControllerApi(apiClient);
        this.inboxId = UUID.fromString(inboxId);
    }

    @Override
    public String waitForConfirmationCode() {
        try {
            OffsetDateTime since = OffsetDateTime.now();
            LOG.info("Ждём новое письмо в inboxId={} с таймаутом {} мс", inboxId, TIMEOUT_MS);

            Email email = waitForControllerApi.waitForLatestEmail(
                    inboxId,
                    TIMEOUT_MS,
                    true,
                    null,
                    since,
                    null,
                    2_000L,
                    null
            );

            String body = email.getBody() == null ? "" : email.getBody();
            String code = extractCode(body);
            LOG.info("Код из письма успешно получен");
            return code;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось получить код подтверждения из MailSlurp за 30 секунд. Проверьте inboxId/API key и отправку письма.",
                    e
            );
        }
    }

    private String extractCode(String body) {
        Matcher matcher = CODE_PATTERN.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalStateException("В теле письма не найден 6-значный код подтверждения");
    }
}
