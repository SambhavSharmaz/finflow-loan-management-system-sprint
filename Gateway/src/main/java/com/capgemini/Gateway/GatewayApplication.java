package com.capgemini.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import java.net.URI;
import java.util.function.Function;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@SpringBootApplication
public class GatewayApplication {

    private final String authServiceUrl;
    private final String applicationServiceUrl;
    private final String documentServiceUrl;
    private final String adminServiceUrl;

    public GatewayApplication(
            @Value("${AUTH_SERVICE_URL:http://localhost:8081}") String authServiceUrl,
            @Value("${APPLICATION_SERVICE_URL:http://localhost:8082}") String applicationServiceUrl,
            @Value("${DOCUMENT_SERVICE_URL:http://localhost:8083}") String documentServiceUrl,
            @Value("${ADMIN_SERVICE_URL:http://localhost:8084}") String adminServiceUrl) {
        this.authServiceUrl = authServiceUrl;
        this.applicationServiceUrl = applicationServiceUrl;
        this.documentServiceUrl = documentServiceUrl;
        this.adminServiceUrl = adminServiceUrl;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouterFunction<?> gatewayRoutes() {
        RouterFunction<ServerResponse> authRoutes = route("auth-service")
                .route(path("/gateway/auth").or(path("/gateway/auth/**")), http())
                .before(url(authServiceUrl))
                .before(stripPrefix(1))
                .filter(circuitBreaker("authCB", URI.create("forward:/fallback/auth")))
                .build();

        RouterFunction<ServerResponse> applicationRoutes = route("application-service")
                .route(path("/gateway/applications").or(path("/gateway/applications/**")), http())
                .before(url(applicationServiceUrl))
                .before(stripPrefix(1))
                .filter(circuitBreaker("appCB", URI.create("forward:/fallback/app")))
                .build();

        RouterFunction<ServerResponse> documentRoutes = route("document-service")
                .route(path("/gateway/documents").or(path("/gateway/documents/**")), http())
                .before(url(documentServiceUrl))
                .before(stripPrefix(1))
                .filter(circuitBreaker("docCB", URI.create("forward:/fallback/doc")))
                .build();

        RouterFunction<ServerResponse> adminRoutes = route("admin-service")
                .route(path("/gateway/admin").or(path("/gateway/admin/**")), http())
                .before(url(adminServiceUrl))
                .before(stripPrefix(1))
                .filter(circuitBreaker("adminCB", URI.create("forward:/fallback/admin")))
                .build();

        return authRoutes
                .andOther(applicationRoutes)
                .andOther(documentRoutes)
                .andOther(adminRoutes);
    }

    @Bean
    public RouterFunction<?> fallbackRoutes() {
        return org.springframework.web.servlet.function.RouterFunctions.route()
                .route(org.springframework.web.servlet.function.RequestPredicates.path("/fallback/auth"), request -> ServerResponse.ok().body("Auth service is unavailable."))
                .route(org.springframework.web.servlet.function.RequestPredicates.path("/fallback/app"), request -> ServerResponse.ok().body("Application service is unavailable."))
                .route(org.springframework.web.servlet.function.RequestPredicates.path("/fallback/doc"), request -> ServerResponse.ok().body("Document service is unavailable."))
                .route(org.springframework.web.servlet.function.RequestPredicates.path("/fallback/admin"), request -> ServerResponse.ok().body("Admin service is unavailable."))
                .build();
    }

    private Function<ServerRequest, ServerRequest> url(String targetUrl) {
        return request -> {
            MvcUtils.setRequestUrl(request, URI.create(targetUrl));
            return request;
        };
    }
}
