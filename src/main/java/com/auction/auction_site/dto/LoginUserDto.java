package com.auction.auction_site.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginUserDto {
    private String username;
    private String password;
}
