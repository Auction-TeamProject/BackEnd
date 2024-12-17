package com.auction.auction_site.service;

import com.auction.auction_site.dto.RegisterUserDto;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public User joinProcess(RegisterUserDto userDto) {
        if(userRepository.existsByUsername(userDto.getUsername())) { // 중복 검사
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        } else {
            User user = new User();

            user.setUsername(userDto.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
            user.setNickname(userDto.getNickname());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setAddress(userDto.getAddress());
            user.setAccountNumber(userDto.getAccountNumber());
            user.setRole("ROLE_USER");

            userRepository.save(user);

            return user;
        }
    }
}
