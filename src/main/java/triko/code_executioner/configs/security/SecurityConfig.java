package triko.code_executioner.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;
	
	@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
        	.authenticationManager(authenticationManager)
        	.securityContextRepository(securityContextRepository)
            .authorizeExchange(exchanges -> exchanges
            	.pathMatchers("/auth/signup").permitAll()
            	.pathMatchers("/auth/signin").permitAll()
                .pathMatchers("/uploads/**").permitAll()
                .pathMatchers("/static/**").permitAll()
                .anyExchange().authenticated()
            )
            .exceptionHandling((exchange) -> {
            	exchange.authenticationEntryPoint((swe, e) -> 
            		Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
            	exchange.accessDeniedHandler((swe, e) -> 
            		Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)));
            })
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .build();
    }
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
