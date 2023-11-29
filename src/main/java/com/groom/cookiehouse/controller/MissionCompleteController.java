package com.groom.cookiehouse.controller;

import com.groom.cookiehouse.common.dto.BaseResponse;
import com.groom.cookiehouse.config.resolver.UserId;
import com.groom.cookiehouse.controller.dto.request.mission.MissionCompleteRequestDto;
import com.groom.cookiehouse.controller.dto.response.mission.CreateMissionCompleteResponseDto;
import com.groom.cookiehouse.controller.dto.response.mission.ReadAllMissionCompleteResponseDto;
import com.groom.cookiehouse.controller.dto.response.mission.ReadMissionCompleteResponseDto;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.SuccessCode;
import com.groom.cookiehouse.exception.model.BadRequestException;
import com.groom.cookiehouse.external.client.aws.S3Service;
import com.groom.cookiehouse.service.mission.MissionCompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/mission-complete")
public class MissionCompleteController {
    private final S3Service s3Service;
    private final MissionCompleteService missionCompleteService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<CreateMissionCompleteResponseDto> createMissionComplete(
            @UserId Long userId,
            @Valid @ModelAttribute final MissionCompleteRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(ErrorCode.REQUEST_VALIDATION_EXCEPTION, ErrorCode.REQUEST_VALIDATION_EXCEPTION.getMessage());
        }

        String imageUrl = s3Service.uploadImage(requestDto.getMissionCompleteImage(), "mission_complete");
        final CreateMissionCompleteResponseDto data = missionCompleteService.createMissionComplete(requestDto, userId, imageUrl);
        return BaseResponse.success(SuccessCode.MISSION_COMPLETE_CREATED_SUCCESS, data);
    }

    @PutMapping(value = "/{missionCompleteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<CreateMissionCompleteResponseDto> updateMissionComplete(
            @UserId Long userId,
            @PathVariable Long missionCompleteId,
            @Valid @ModelAttribute final MissionCompleteRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(ErrorCode.REQUEST_VALIDATION_EXCEPTION, ErrorCode.REQUEST_VALIDATION_EXCEPTION.getMessage());
        }

        final CreateMissionCompleteResponseDto data = missionCompleteService.updateMissionComplete(userId, missionCompleteId, requestDto);
        return BaseResponse.success(SuccessCode.MISSION_COMPLETE_UPDATED_SUCCESS, data);
    }

    @GetMapping("/{missionCompleteId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<ReadMissionCompleteResponseDto> getMissionComplete(@PathVariable Long missionCompleteId) {
        final ReadMissionCompleteResponseDto data = missionCompleteService.findMissionComplete(missionCompleteId);
        return BaseResponse.success(SuccessCode.GET_MISSION_COMPLETE_SUCCESS, data);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<ReadAllMissionCompleteResponseDto> getAllMissionComplete(@RequestParam Long userId) {
        final ReadAllMissionCompleteResponseDto data = missionCompleteService.findAllMissionComplete(userId);
        return BaseResponse.success(SuccessCode.GET_MISSION_COMPLETE_SUCCESS, data);
    }

}
