package com.auction.auction_site.service;

import com.auction.auction_site.dto.RegisterUserDto;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterUserDto RegisterProcess(RegisterUserDto registerUserDto) {
        if(userRepository.existsByUsername(registerUserDto.getUsername())) { // 중복 검사
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .username(registerUserDto.getUsername())
                .password(bCryptPasswordEncoder.encode(registerUserDto.getPassword()))
                .nickname(registerUserDto.getNickname())
                .phoneNumber(registerUserDto.getPhoneNumber())
                .address(registerUserDto.getAddress())
                .accountNumber(registerUserDto.getAccountNumber())
                .build();

        userRepository.save(user);

        return RegisterUserDto.fromUser(user);
    }
}
