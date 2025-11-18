package samukadev.coderpg.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import samukadev.coderpg.domain.exceptions.GitHubWebhookException;
import samukadev.coderpg.infrastructure.github.config.GitHubProperties;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.routes.WebhooksRoute;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final GitHubProperties gitHubProperties;
    private final ObjectMapper objectMapper;

    @PostMapping(WebhooksRoute.GITHUB)
    public ResponseEntity<ApiResponse<String>> handleGitHubWebhook(
            @RequestBody String payload,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId
    ) {

        if (!validateSignature(payload, signature)) {
            throw new GitHubWebhookException("Invalid webhook signature");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);

            log.debug("Webhook payload keys: {}", payloadMap.keySet());

            return ResponseEntity.ok(ApiResponse.success(
                    "Webhook received successfully",
                    "Event: " + eventType
            ));
        } catch (Exception e) {
            throw new GitHubWebhookException("Webhook received failure: " + e.getMessage());
        }

    }

    private boolean validateSignature(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) {
            return false;
        }

        try {
            String webhookSecret = gitHubProperties.getWebhookSecret();
            if (webhookSecret == null || webhookSecret.isBlank()) {
                return false;
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = "sha256=" + bytesToHex(hash);

            boolean isValid = calculatedSignature.equals(signature);

            if (!isValid) {
                log.warn("Signature mismatch - Expected: {}, Got: {}",
                        calculatedSignature, signature);
            }

            return isValid;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
