package com.auction.auction_site.config;

import com.auction.auction_site.security.jwt.JWTFilter;
import com.auction.auction_site.security.jwt.JWTUtil;
import com.auction.auction_site.security.oauth.CustomSuccessHandler;
import com.auction.auction_site.security.oauth.CustomOAuth2UserService;
import com.auction.auction_site.security.spring_security.CustomJsonLoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 스프링 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin((formLogin) -> formLogin.disable());

        httpSecurity.httpBasic((httpBasic) -> httpBasic.disable());

        httpSecurity.csrf((auth) -> auth.disable());

        httpSecurity.addFilterBefore(
                new CustomJsonLoginFilter(authenticationManager(httpSecurity), objectMapper, jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        httpSecurity.addFilterAfter(new JWTFilter(jwtUtil), CustomJsonLoginFilter.class);

        httpSecurity.oauth2Login(
                (ouath2) -> ouath2
                        .userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
        );

        httpSecurity.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**", "/members").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );

        httpSecurity.sessionManagement(
                (session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }
}
