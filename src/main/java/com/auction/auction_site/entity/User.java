package com.auction.auction_site.entity;

import com.auction.auction_site.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UNIQUE",
                columnNames = {"password", "nickname", "phoneNumber", "accountNumber"})})
@Getter @Setter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

    private String nickname;

    private String phoneNumber;

    private String address;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    public User() {}
}
