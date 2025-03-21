package com.auction.auction_site.dto.member;

import com.auction.auction_site.entity.Member;
import com.auction.auction_site.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {
    private String loginId;

    private String password;

    private String nickname;

    private String email;

    private Role role;

    public MemberDto() {}

    // 엔티티 → DTO 변환 메서드
    public static MemberDto fromMember(Member member) {
        return MemberDto.builder()
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole())
                .build();
    }
}


