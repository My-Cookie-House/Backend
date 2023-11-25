package com.groom.cookiehouse.service.auth;

import com.groom.cookiehouse.controller.dto.response.auth.TokenResponseDto;
import com.groom.cookiehouse.oauth2.ClientRegistration;
import com.groom.cookiehouse.oauth2.ClientRegistrationRepository;
import com.groom.cookiehouse.oauth2.OAuth2Token;
import com.groom.cookiehouse.oauth2.service.OAuth2Service;
import com.groom.cookiehouse.oauth2.userInfo.OAuth2UserInfo;
import com.groom.cookiehouse.config.jwt.JwtService;
import com.groom.cookiehouse.controller.dto.response.auth.SignInResponseDto;
import com.groom.cookiehouse.domain.user.SocialType;
import com.groom.cookiehouse.domain.user.User;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.model.NotFoundException;
import com.groom.cookiehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;

    private final Long TOKEN_EXPIRATION_TIME_ACCESS = 100 * 24 * 60 * 60 * 1000L; // 100일
    private final Long TOKEN_EXPIRATION_TIME_REFRESH = 200 * 24 * 60 * 60 * 1000L; // 200일

    private final UserRepository userRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    public final RestTemplate restTemplate;

    private final HttpSession httpSession;

    public SignInResponseDto login(String code, String provider, String state) { //로그인
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider); //provider. 서비스 제공자에 대한 등록 정보를 저장하고 검색
        OAuth2Service oAuth2Service = new OAuth2Service(restTemplate, httpSession); //OAuth 2.0 프로토콜과 관련된 서비스를 제공하는 클래스

        OAuth2Token oAuth2Token = oAuth2Service.getAccessToken(clientRegistration, code, state); //액세스 토큰을 갖고옴
        OAuth2UserInfo oAuth2UserInfo = oAuth2Service.getUserInfo(clientRegistration, oAuth2Token.getToken());
        return signIn(oAuth2UserInfo, provider, oAuth2Token.getSocialToken());
    }
    @Transactional
    public SignInResponseDto signIn(OAuth2UserInfo oAuth2UserInfo, String provider, String socialToken) {
        SocialType socialType = SocialType.valueOf(provider.toUpperCase());
        String socialId = oAuth2UserInfo.getId();
        String userName = oAuth2UserInfo.getName();
        Boolean isRegistered = userRepository.existsBySocialIdAndSocialType(socialId, socialType);

        if (!isRegistered) {
            User newUser = User.builder()
                    .userName(userName)
                    .socialId(socialId)
                    .socialType(socialType)
                    .socialToken(socialToken)
                    .build();

            User user = userRepository.save(newUser);
            System.out.println(user.getUserName());
        }

        User user = userRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        String accessToken = jwtService.issuedToken(String.valueOf(user.getId()), TOKEN_EXPIRATION_TIME_ACCESS);
        String refreshToken = jwtService.issuedToken(String.valueOf(user.getId()), TOKEN_EXPIRATION_TIME_REFRESH);

        // 세션에 socialRefreshToken 저장
        httpSession.setAttribute("socialToken", socialToken);
        System.out.println("!!!!!!!!!!!!!!!!" + socialToken);

        System.out.println("리프레쉬 발급");
        user.updateRefreshToken(refreshToken);

        System.out.println(refreshToken);
        System.out.println("리프레쉬 저장");

        System.out.println(user.getRefreshToken());
        userRepository.save(user);

        Boolean isHouseBuilt = true;
        if (user.getHouseName() == null) {
            isHouseBuilt = false;
        }
        return SignInResponseDto.of(user.getId(), user.getUserName(), accessToken, socialToken, isRegistered, isHouseBuilt);
    }

    @Transactional
    public TokenResponseDto issueToken(String refreshToken) {
        jwtService.verifyToken(refreshToken);

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        String newAccessToken = jwtService.issuedToken(String.valueOf(user.getId()), TOKEN_EXPIRATION_TIME_ACCESS);
        String newRefreshToken = jwtService.issuedToken(String.valueOf(user.getId()), TOKEN_EXPIRATION_TIME_REFRESH);

        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);
        return TokenResponseDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void signOut(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        user.updateRefreshToken(null);
    }
    public String getUserAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        return user.getSocialToken();
    }
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        userRepository.delete(user);
    }
}
