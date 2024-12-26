package com.auction.auction_site.security.oauth;

import com.auction.auction_site.dto.*;
import com.auction.auction_site.dto.oauth.GoogleResponse;
import com.auction.auction_site.dto.oauth.KakaoResponse;
import com.auction.auction_site.dto.oauth.NaverResponse;
import com.auction.auction_site.dto.oauth.OAuth2Response;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    /**
     * 네이버나 구글의 사용자 정보 데이터를 인자로 받아옴
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User.getAttributes());

        String registrationId= userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        // 네이버, 구글, 카카오에서 보내는 인증 데이터 규격이 다르므로 각각의 응답을 처리하는 DTO 생성
        switch (registrationId) {
            case "naver":
                oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
                break;
            case "google":
                oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
                break;
            case "Kakao":
                oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
                break;
            default:
                    return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        User findUser = userRepository.findByUsername(username);

        UserDto userDto = null;

        // 유저 정보가 없으면 저장
        if(findUser == null) {
            User user = User.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .password(oAuth2Response.getProviderId()) // 제공자 내부에서 사용자를 식별하는 고유 ID - 일단 비밀번호로 처리
                    .build();

            userRepository.save(user);

            userDto = UserDto.fromUser(user);

        } else { // 유저 정보가 있으면 업데이트
            findUser.setName(oAuth2Response.getName());
            findUser.setEmail(oAuth2Response.getEmail());

            userRepository.save(findUser);

            userDto = UserDto.fromUser(findUser);
        }

        return new CustomOAuth2User(userDto);
    }
}