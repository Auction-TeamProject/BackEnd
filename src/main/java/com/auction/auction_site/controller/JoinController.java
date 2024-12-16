package com.auction.auction_site.controller;

import com.auction.auction_site.dto.UserDto;
import com.auction.auction_site.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;

    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @PostMapping("/join")
    public String join(UserDto userDto) {
        joinService.joinProcess(userDto);

        return "redirect:/login";
    }
}
