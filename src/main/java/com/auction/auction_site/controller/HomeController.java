package com.auction.auction_site.controller;

import com.auction.auction_site.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<ApiResponse> home() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Iterator<? extends GrantedAuthority> iterator = authentication.getAuthorities().iterator();
        String role = iterator.next().getAuthority();

        ApiResponse response = new ApiResponse(
                "home page",
                Map.of("id", id, "role", role));

        return ResponseEntity.ok(response);
    }
}
