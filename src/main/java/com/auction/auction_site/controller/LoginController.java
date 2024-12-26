package com.auction.auction_site.controller;

import com.auction.auction_site.dto.ApiResponse;
import com.auction.auction_site.dto.LoginUserDto;
import com.auction.auction_site.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    @GetMapping
    public String login() {
        return "login page";
    }

//    @PostMapping
//    public ResponseEntity<ApiResponse> login(@RequestBody LoginUserDto loginUserDto) {
//        loginService.authenticate(loginUserDto.getUsername(), loginUserDto.getPassword());
//
//        // ApiResponse 생성
//        ApiResponse response = new ApiResponse("로그인 성공", loginUserDto.getUsername());
//
//        return ResponseEntity.ok(response);
//    }
}
