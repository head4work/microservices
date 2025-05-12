package com.example.gateway.security;

import com.example.gateway.exceptions.CustomJwtException;
import com.example.gateway.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtFilter implements GlobalFilter, Ordered {
    // Define paths to skip (can use regex or path matcher if needed)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/service/v1/auth", "/auth/login", "/auth/register", "/auth/refresh"
    );
    private final JwtService jwtUtil;

    public JwtFilter(JwtService jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getURI().getPath();

        // Skip filter if path matches
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Unauthorized");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            String email = claims.getSubject();
            String userId = claims.get("userId", String.class);
            List<String> roles = claims.get("roles", List.class);

            // Mutate request with new headers
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-ID", userId)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            return chain.filter(mutatedExchange)
                    .onErrorResume(e -> handleError(exchange, e)); // Reactive error handling

        } catch (CustomJwtException e) {
            return unauthorized(exchange, e.getMessage());
        } catch (Exception e) {
            return unauthorized(exchange, "Unauthorized: " + e.getMessage());
        }
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof ExpiredJwtException || ex instanceof CustomJwtException) {
            return unauthorized(exchange, ex.getMessage());
        }

        // Let non-auth errors propagate
        return Mono.error(ex);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Run early
    }
}
