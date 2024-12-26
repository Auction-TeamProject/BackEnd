package com.auction.auction_site.controller;

import com.auction.auction_site.dto.ApiResponse;
import com.auction.auction_site.dto.UserDto;
import com.auction.auction_site.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원 목록
     */
    @GetMapping
    public String register() {
        return "register page";
    }

    /**
     * 회원 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse> register(@RequestBody UserDto userDto) {
        UserDto user = memberService.registerProcess(userDto);

        // ApiResponse 생성
        ApiResponse response = new ApiResponse(
                "회원가입 완료", // message
                Map.of( // 사용자 데이터
                        "username", user.getUsername(),
//                        "nickname", user.getNickname(),
                        "role", user.getRole()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> modify(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto user = memberService.updateMember(id, userDto);

        // ApiResponse 생성
        ApiResponse response = new ApiResponse(
                "회원수정 완료", // message
                Map.of( // 사용자 데이터
                        "username", user.getUsername(),
//                        "nickname", user.getNickname(),
                        "role", user.getRole()
                )
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        memberService.deleteProcess(id, request, response);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
