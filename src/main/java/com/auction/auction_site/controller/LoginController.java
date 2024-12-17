package com.auction.auction_site.controller;

import com.auction.auction_site.dto.ApiResponse;
import com.auction.auction_site.dto.LoginUserDto;
import com.auction.auction_site.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginUserDto userDto) {
        loginService.authenticate(userDto.getUsername(), userDto.getPassword());

        // ApiResponse 생성
        ApiResponse response = new ApiResponse("로그인 성공", userDto.getUsername());

        return ResponseEntity.ok(response);
    }
}
