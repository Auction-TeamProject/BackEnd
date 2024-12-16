package com.auction.auction_site.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {
    private String username;

    private String password;

    private String nickname;

    private String phoneNumber;

    private String address;

    private String accountNumber;

    private String role;
}
