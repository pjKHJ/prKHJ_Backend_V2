package demago.khjv2.global.feignclient.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendService {

    private final DiscordWebhookClient discordWebhookClient;

    @Async("discordAsyncExecutor") // 비동기처리
    public void sendDiscordAlert(Exception e, String method, String uri) {
        try {
            String message = buildMessage(e, method, uri);
            discordWebhookClient.send(
                    new DiscordWebhookRequest(message)
            );
        } catch (Exception ex) {
            log.error("Failed to send discord webhook", ex);
        }
    }

    public String buildMessage(Exception e, String method, String uri) {
        String exceptionMsg = e.getMessage() != null ? e.getMessage() : "No message available";
        String errorMessage = """
            ## Exception Alert
            - Method: `%s`
            - URI: `%s`
            - Exception: `%s`
            - Message: %s
            """.formatted(method, uri, e.getClass().getSimpleName(), exceptionMsg);

        // 2000자 이상이면 자름
        if (errorMessage.length() > 2000) {
            return errorMessage.substring(0, 1990) + "\n... (중략)";
        }

        return errorMessage;
    }
}
