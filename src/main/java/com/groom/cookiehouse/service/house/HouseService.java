package com.groom.cookiehouse.service.house;

import com.groom.cookiehouse.controller.dto.response.house.HouseResponseDto;
import com.groom.cookiehouse.domain.user.User;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.model.NotFoundException;
import com.groom.cookiehouse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final UserRepository userRepository;

    @Transactional
    public void createHouse(Long userId, Long icingId, List<Long> cookieIds, String houseName, Long wallpaperId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        user.updateHouse(houseName, cookieIds.get(0), cookieIds.get(1), icingId, wallpaperId);
    }

    public HouseResponseDto getHouse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        List<Long> cookieIds = new ArrayList<>();
        cookieIds.add(user.getCookieOne());
        cookieIds.add(user.getCookieTwo());
        return HouseResponseDto.of(
                user.getIcing(),
                cookieIds,
                user.getHouseName(),
                user.getWallpaper()
        );
    }

}
