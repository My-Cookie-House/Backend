package com.groom.cookiehouse.controller;

import com.groom.cookiehouse.common.dto.BaseResponse;
import com.groom.cookiehouse.config.resolver.UserId;
import com.groom.cookiehouse.controller.dto.response.mission.MissionResponseDto;
import com.groom.cookiehouse.exception.SuccessCode;
import com.groom.cookiehouse.service.mission.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/today-mission")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<MissionResponseDto> getTodayMission(@UserId Long userId) {
        final MissionResponseDto data = missionService.getTodayMission(userId);
        return BaseResponse.success(SuccessCode.GET_MISSION_SUCCESS, data);
    }

}
