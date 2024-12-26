package com.auction.auction_site.dto;

import com.auction.auction_site.entity.Role;
import com.auction.auction_site.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter @Setter
public class UserDto {
    private String username;

    private String name;

    private String email;

    private String password;

//    private String nickname;

    private Role role;

    // 엔티티 → DTO 변환 메서드
    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
//                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
