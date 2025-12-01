package samukadev.coderpg.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import samukadev.coderpg.core.integration.github.GitHubWebHookHandlerPort;
import samukadev.coderpg.domain.exceptions.GitHubWebhookException;
import samukadev.coderpg.domain.github.GitHubWebhookPayload;
import samukadev.coderpg.infrastructure.github.config.GitHubProperties;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.routes.WebhooksRoute;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final GitHubProperties gitHubProperties;
    private final ObjectMapper objectMapper;
    private final GitHubWebHookHandlerPort webhookHandler;

    @PostMapping(WebhooksRoute.GITHUB)
    public ResponseEntity<ApiResponse<String>> handleGitHubWebhook(
            HttpServletRequest request,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId
    ) {
        String payload = getRawPayload(request);

        log.info("Payload: {}", payload);

        if (!validateSignature(payload, signature)) {
            throw new GitHubWebhookException("Invalid webhook signature!");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);

            GitHubWebhookPayload webhookPayload = GitHubWebhookPayload.builder()
                    .eventType(eventType)
                    .deliveryId(deliveryId)
                    .signature(signature)
                    .payload(payloadMap)
                    .rawPayload(payload)
                    .build();

            webhookHandler.processWebhook(webhookPayload, eventType);

            return ResponseEntity.ok(ApiResponse.success(
                    "Webhook accepted",
                    "Processing event: " + eventType
            ));

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            throw new GitHubWebhookException("Failed to process webhook: " + e.getMessage(), e);
        }
    }

    private boolean validateSignature(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) {
            log.info("Falha de validação: Assinatura nula ou formato incorreto. Signature: {}", signature);
            return false;
        }

        try {
            String webhookSecret = gitHubProperties.getWebhookSecret();
            if (webhookSecret == null || webhookSecret.isBlank()) {
                log.error("Falha de validação: Webhook Secret não configurado/nulo.");
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
            log.error("Hash algorithm error.", e);
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

    private String getRawPayload(HttpServletRequest request) {
        try {
            return request.getReader().lines()
                    .collect(Collectors.joining());
        } catch (IOException e) {
            log.error("Failed to read raw payload from request", e);
            return null;
        }
    }
}