package com.auction.auction_site.config;

import com.auction.auction_site.repository.RefreshTokenRepository;
import com.auction.auction_site.security.jwt.JWTFilter;
import com.auction.auction_site.security.jwt.JWTUtil;
import com.auction.auction_site.security.oauth.CustomSuccessHandler;
import com.auction.auction_site.security.oauth.CustomOAuth2UserService;
import com.auction.auction_site.security.spring_security.CustomJsonLoginFilter;
import com.auction.auction_site.security.spring_security.CustomLogoutFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin((formLogin) -> formLogin.disable());

        httpSecurity.httpBasic((httpBasic) -> httpBasic.disable());

        httpSecurity.csrf((auth) -> auth.disable());

        httpSecurity.addFilterBefore(new CustomJsonLoginFilter(
                authenticationManager(authenticationConfiguration), objectMapper, jwtUtil, refreshTokenRepository),
                UsernamePasswordAuthenticationFilter.class);

        httpSecurity.addFilterAfter(new JWTFilter(jwtUtil), CustomJsonLoginFilter.class);

        httpSecurity.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

        httpSecurity.oauth2Login(
                (ouath2) -> ouath2
                        .userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
        );

        httpSecurity.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**", "/members", "/reissue").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/{id}","/products" ).permitAll() // GET 요청에 대해 모든 사용자 허용


                        .anyRequest().authenticated()
        );

        httpSecurity.sessionManagement(
                (session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }
}
