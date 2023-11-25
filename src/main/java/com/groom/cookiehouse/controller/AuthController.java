package com.groom.cookiehouse.controller;

import com.groom.cookiehouse.common.dto.BaseResponse;
import com.groom.cookiehouse.config.resolver.UserId;
import com.groom.cookiehouse.controller.dto.response.auth.SignInResponseDto;
import com.groom.cookiehouse.controller.dto.response.auth.TokenResponseDto;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.SuccessCode;
import com.groom.cookiehouse.oauth2.userInfo.OAuth2UserInfo;
import com.groom.cookiehouse.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final HttpSession httpSession;

    @GetMapping("/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<SignInResponseDto> login(@PathVariable String provider, @RequestParam String code, @RequestParam String state) {
        return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, authService.login(code, provider, state));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<SignInResponseDto> signIn() {
        OAuth2UserInfo userInfo = (OAuth2UserInfo) httpSession.getAttribute("oAuth2UserInfo");
        String provider = (String) httpSession.getAttribute("provider");
        String accessToken = (String) httpSession.getAttribute("accessToken");
        return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, authService.signIn(userInfo, provider, accessToken));
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<TokenResponseDto> reissueToken(@RequestHeader String refreshToken) {
        return BaseResponse.success(SuccessCode.RE_ISSUE_TOKEN_SUCCESS, authService.issueToken(refreshToken));
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse signOut(@UserId Long userId) {
        authService.signOut(userId);
        return BaseResponse.success(SuccessCode.SIGNOUT_SUCCESS);
    }
    @PostMapping("/unlink")
    public BaseResponse<String> unlinkKakaoAccount(@RequestParam Long userId) {
        try {
            // 카카오 액세스 토큰 가져오기
            String socialToken = authService.getUserAccessToken(userId);
            System.out.println("!!!!!!!!!!!"+socialToken);
            // 카카오에 회원 탈퇴 요청 보내기
            String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");
            headers.set("Authorization", "Bearer " + socialToken);
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate1 = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate1.exchange(unlinkUrl, HttpMethod.POST, requestEntity, String.class);
            authService.deleteUser(userId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return BaseResponse.ok("카카오 계정 unlink 성공");

            } else {
                return BaseResponse.error(ErrorCode.valueOf("카카오 계정 unlink 실패"), responseEntity.getStatusCode().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error(ErrorCode.valueOf("카카오 계정 unlink 실패"), e.getMessage());
        }
    }
}