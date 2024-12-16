package com.auction.auction_site.service;

import com.auction.auction_site.dto.UserDto;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public void joinProcess(UserDto userDto) {
        if(userRepository.existsByUsername(userDto.getUsername())) { // 중복 검사
            System.out.println("이미 회원 존재");
        } else {
            User user = new User();

            user.setUsername(userDto.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
            user.setNickname(userDto.getNickname());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setAddress(userDto.getAddress());
            user.setAccountNumber(userDto.getAccountNumber());
            user.setRole("ROLE_ADMIN");

            userRepository.save(user);
        }
    }
}
