package samukadev.coderpg.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.security.oauth.GitHubOAuthService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepositoryPort userRepository;
    private final GitHubOAuthService gitHubOAuthService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && !token.isBlank()) {
                log.debug("🔑 Token encontrado para {}: {}", requestPath, token.substring(0, 8) + "...");
                authenticateUser(token, request);
            } else {
                log.debug("⚠️ Nenhum token encontrado para {}", requestPath);
            }
        } catch (Exception e) {
            log.error("❌ Error authenticating user from token for {}: {}", requestPath, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void authenticateUser(String userId, HttpServletRequest request) {
        try {
            log.debug("🔍 Tentando autenticar usuário com ID: {}", userId.substring(0, 8) + "...");

            UUID userUuid = UUID.fromString(userId);
            Optional<User> userOpt = userRepository.get(userUuid);

            if (userOpt.isEmpty()) {
                log.warn("⚠️ User not found for ID: {}", userId.substring(0, 8) + "...");
                return;
            }

            User user = userOpt.get();
            log.debug("✅ Usuário encontrado: {} (githubId: {})", user.getGithubUsername(), user.getGithubId());

            boolean hasValidToken = gitHubOAuthService.hasValidToken(user.getId());

            if (!hasValidToken) {
                log.warn("⚠️ User {} does not have a valid GitHub token", userId.substring(0, 8) + "...");
                return;
            }

            log.debug("✅ Token do GitHub válido para usuário {}", user.getGithubUsername());

            // IMPORTANTE: Coloca o githubId como "name" da autenticação
            // O AuthController usa authentication.getName() para buscar o user
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getGithubId().toString(),  // ← githubId aqui
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("✅ User {} authenticated successfully via token", userId.substring(0, 8) + "...");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid UUID format: {}", userId);
        } catch (Exception e) {
            log.error("❌ Error during authentication: {}", e.getMessage(), e);
        }
    }
}