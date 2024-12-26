package com.auction.auction_site.filter;

import com.auction.auction_site.dto.CustomUserDetails;
import com.auction.auction_site.dto.LoginUserDto;
import com.auction.auction_site.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    private final JWTUtil jwtUtil;

    // 해당 필터가 적용되는 요청
    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";

    // 요청 매핑 조건
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JWTUtil jwtUtil) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }

    // 로그인 요청이 들어올 때 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 요청 Content-Type 확인
        if (!CONTENT_TYPE.equalsIgnoreCase(request.getContentType())) {
            throw new AuthenticationServiceException("Unsupported Content-Type: " + request.getContentType());
        }

        // JSON 데이터를 LoginUserDto 객체로 변환
        LoginUserDto loginDto = objectMapper.readValue(request.getInputStream(), LoginUserDto.class);

        // 유효성 검사
        if (!StringUtils.hasText(loginDto.getUsername()) || !StringUtils.hasText(loginDto.getPassword())) {
            throw new AuthenticationServiceException("Username or Password is missing");
        }

        // Authentication 생성
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 인증 요청 객체에 추가 정보 설정??
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        /*
        // 세션

        // 인증 성공 시, SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authResult);

        // 세션 생성 및 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        // 기본적인 성공 응답
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful with session ID: " + session.getId());
        */

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 60*60*1000L);

//        response.addCookie(createCookie("Authorization", token));

        response.addHeader("Authorization", "Bearer " + token);

        response.getWriter().write("Login successful with session ID");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(401);
    }

//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(60*60*60);
//        //cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }
}
