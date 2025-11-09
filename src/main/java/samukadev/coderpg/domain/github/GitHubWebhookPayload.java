package samukadev.coderpg.domain.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitHubWebhookPayload {
    private String eventType;
    private String deliveryId;
    private String signature;
    private Map<String, Object> payload;
    private String rawPayload;

    public <T> T getPayloadField(String key, Class<T> type) {
        if (payload == null || !payload.containsKey(key)) {
            return null;
        }
        Object value = payload.get(key);
        return type.cast(value);
    }
}
