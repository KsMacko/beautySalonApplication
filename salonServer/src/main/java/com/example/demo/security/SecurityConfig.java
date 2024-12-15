package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


//Этот класс конфигурации используется для настройки
// безопасности вашего веб-приложения с использованием
// Spring Security. Он определяет, как будут обрабатываться
// запросы, как будет производиться аутентификация, а также
// различные аспекты безопасности, такие как выход из системы
// и защита от CSRF.
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    @Qualifier("customConfig")
    private UserDetailsSer userDetailsSer;
    @Autowired private PasswordEncoder passwordEncoder;

    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/auth/toSignIn").permitAll()
                .requestMatchers("/auth/toSignUp").permitAll()
                        .requestMatchers("/**").permitAll())
                .logout(logout -> logout .logoutUrl("/api/logout")
                        .logoutSuccessUrl("/auth/toSignIn").permitAll() )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsSer).passwordEncoder(passwordEncoder);
    }
}
