package srk.webfluxsecurity.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;
import srk.webfluxsecurity.security.AuthenticationManager;
import srk.webfluxsecurity.security.BearerTokenServerAuthenticationConverter;
import srk.webfluxsecurity.security.JwtHandler;


@Slf4j
@Configuration
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String [] publicRouts = {"/api/v1/auth/register", "/api/v1/auth/login"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .pathMatchers(publicRouts)
                .permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    log.error("In securityWebFilterChain - unauthorized error: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                .accessDeniedHandler((swe, e) -> {
                    log.error("In securityWebFilterChain - access denied: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                })
                .and()
                .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }

    private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        bearerAuthenticationFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return bearerAuthenticationFilter;
    }
}
