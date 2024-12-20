package com.auction.auction_site.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/", "/login", "/register").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/myPage/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
        );

        httpSecurity.formLogin((formLogin) -> formLogin.disable());

        httpSecurity.httpBasic((httpBasic) -> httpBasic.disable());

        httpSecurity.csrf((auth) -> auth.disable());

        httpSecurity.sessionManagement( // 다중 로그인 설정
                (auth) -> auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true) // 다중 로그인 개수를 초과하였을 경우 새로운 로그인 차단
        );

        httpSecurity.sessionManagement( // 세션 고정 보호
                (auth) -> auth
                        .sessionFixation()
                        .changeSessionId() // 로그인 시 동일한 세션에 대한 ID 변경
        );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}
