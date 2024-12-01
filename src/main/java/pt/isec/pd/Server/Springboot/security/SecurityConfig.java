package pt.isec.pd.Server.Springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain openEndpointsFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("api/auth/register", "api/auth/list_usernames") // Applies to `/register`
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow everyone
                )
                .build();
    }

    @Bean
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/auth/login", "/api/auth/hello", "/api/auth/authorization") // Applies to `/login`
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Require authentication
                )
                .httpBasic(Customizer.withDefaults()) // Enable Basic Authentication
                .build();
    }

    @Bean
    public SecurityFilterChain bearerTokenFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/groups/**")
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/groups/**").authenticated() // Authenticated users only
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }


}
