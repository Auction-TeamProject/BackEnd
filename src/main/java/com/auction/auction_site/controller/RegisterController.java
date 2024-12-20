package com.auction.auction_site.controller;

import com.auction.auction_site.dto.ApiResponse;
import com.auction.auction_site.dto.RegisterUserDto;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @GetMapping
    public String register() {
        return "register page";
    }

    @PostMapping
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterUserDto registerUserDto) {
        RegisterUserDto user = registerService.RegisterProcess(registerUserDto);

        // ApiResponse 생성
        ApiResponse response = new ApiResponse(
                "회원가입 완료", // message
                Map.of( // 사용자 데이터
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "role", user.getRole()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
