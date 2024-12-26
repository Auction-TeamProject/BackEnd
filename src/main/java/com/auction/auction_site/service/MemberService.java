package com.auction.auction_site.service;

import com.auction.auction_site.exception.EntityNotFound;
import com.auction.auction_site.dto.UserDto;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDto registerProcess(UserDto userDto) {
        if(userRepository.existsByUsername(userDto.getUsername())) { // 중복 검사
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
//                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .build();

        userRepository.save(user);

        return UserDto.fromUser(user);
    }

    public UserDto updateMember(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User with id " + id + " not found"));

        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
//        if (userDto.getNickname() != null) {
//            user.setNickname(userDto.getNickname());
//        }

        userRepository.save(user);

        return UserDto.fromUser(user);
    }

    public void deleteProcess(Long id, HttpServletRequest request, HttpServletResponse response) {
        User findUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User with id " + id + " not found"));

        userRepository.delete(findUser);
    }
}
