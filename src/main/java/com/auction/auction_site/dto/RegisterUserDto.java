package com.auction.auction_site.dto;

import com.auction.auction_site.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter @Setter
public class RegisterUserDto {
    private String username;

    private String password;

    private String nickname;

    private String phoneNumber;

    private String address;

    private String accountNumber;

    private Role role;

    public static RegisterUserDto fromUser(User user) {
        return RegisterUserDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .accountNumber(user.getAccountNumber())
                .role(user.getRole())
                .build();
    }
}
