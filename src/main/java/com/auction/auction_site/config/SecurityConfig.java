package com.auction.auction_site.config;

//import com.auction.auction_site.CustomLogoutSuccessHandler;
import com.auction.auction_site.jwt.JWTFilter;
import com.auction.auction_site.jwt.JWTUtil;
import com.auction.auction_site.oauth2.CustomSuccessHandler;
import com.auction.auction_site.filter.LoginFilter;
import com.auction.auction_site.oauth2.CustomOAuth2UserService;
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
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
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
                new LoginFilter(authenticationManager(httpSecurity), objectMapper, jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        httpSecurity.addFilterAfter(new JWTFilter(jwtUtil), LoginFilter.class);

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
                        .requestMatchers("/myPage/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
        );


        httpSecurity.sessionManagement(
                (session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        httpSecurity.sessionManagement( // 다중 로그인 설정
//                (auth) -> auth
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true) // 다중 로그인 개수를 초과하였을 경우 새로운 로그인 차단
//        );
//
//        httpSecurity.sessionManagement( // 세션 고정 보호
//                (auth) -> auth
//                        .sessionFixation()
//                        .changeSessionId() // 로그인 시 동일한 세션에 대한 ID 변경
//        );
//
//        httpSecurity.logout(logout -> logout
//                        .logoutUrl("/logout") // 로그아웃 요청 URL
////                .logoutSuccessHandler(new CustomLogoutSuccessHandler()) // 커스텀 성공 핸들러
//                        .deleteCookies("JSESSIONID") // 쿠키 삭제
//                        .invalidateHttpSession(true) // 세션 무효화
//        );

        return httpSecurity.build();
    }
}
