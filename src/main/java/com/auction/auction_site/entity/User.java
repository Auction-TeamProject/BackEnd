package com.auction.auction_site.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true) // 중복 방지
    private int id;

    private String username;

    private String password;

    @Column(unique = true) // 중복 방지
    private String nickname;

    private String phoneNumber;

    private String address;

    private String accountNumber;

    private String role;
}
