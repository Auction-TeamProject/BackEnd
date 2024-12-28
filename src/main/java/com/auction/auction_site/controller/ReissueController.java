package com.auction.auction_site.controller;

import com.auction.auction_site.entity.RefreshToken;
import com.auction.auction_site.repository.RefreshTokenRepository;
import com.auction.auction_site.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.auction.auction_site.config.ConstantConfig.*;

/**
 * ReissueController
 * - 해당 컨트롤러는 전달받은 Refresh 토큰을 받아 새로운 Access 토큰을 응답하는 컨트롤러
 *
 * - 도입이유
 * 서버측에서 JWTFilter 해당 필터를 통해 Access 토큰 만료로 인한 특정 상태 코드를 응답하면?
 * 프론트측에서 예외 핸들라러 Access 토큰 재발행을 위한 Refresh 토큰을 전달
 * 서버측은 전달받은 Refresh 토큰을 받아 새로운 Access 토큰을 응답
 *
 * + 추가
 * 보안을 위해 Access 토큰 뿐 아니라 Refresh 토큰도 함께 갱신(Refresh Rotate)
 * 단, Rotate 되기 전의 토큰을 탈취 했을 경우 해당 토큰으로도 인증되므로 서버측에서 발급했던 Refresh 추가 처리 작업 필요
 */
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;


        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null) { // Refresh 토큰의 널 체크
            return new ResponseEntity<>("refresh token is empty", HttpStatus.BAD_REQUEST);
        }

        try { // Refresh 토큰의 만료여부 확인
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refreshToken);

        if(!category.equals("refresh")) { // JWT 토큰이 Refresh 토큰인지 확인
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRED_MS);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRED_MS);

        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .refreshToken(newRefreshToken)
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRED_MS))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(true); // 자바스크립트 공격 방지

        return cookie;
    }
}