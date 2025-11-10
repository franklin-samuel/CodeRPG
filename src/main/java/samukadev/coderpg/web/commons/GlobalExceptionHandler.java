package samukadev.coderpg.web.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.domain.exceptions.GitHubApiException;
import samukadev.coderpg.domain.exceptions.GitHubWebhookException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                "BUSINESS_ERROR"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleGitHubApiException(
            GitHubApiException ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                "GITHUB_API_ERROR"
        );

        HttpStatus status = ex.getStatusCode() >= 500
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(GitHubWebhookException.class)
    public ResponseEntity<ApiResponse<Void>> handleGitHubWebhookException(
            GitHubWebhookException ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                "WEBHOOK_ERROR"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .data(errors)
                .error("Validation failed")
                .code("VALIDATION_ERROR")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                "Access denied",
                "ACCESS_DENIED"
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                "INVALID_ARGUMENT"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred",
                "INTERNAL_ERROR"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}