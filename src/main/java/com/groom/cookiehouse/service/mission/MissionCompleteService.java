package com.groom.cookiehouse.service.mission;

import com.groom.cookiehouse.controller.dto.request.mission.MissionCompleteRequestDto;
import com.groom.cookiehouse.controller.dto.response.mission.CreateMissionCompleteResponseDto;
import com.groom.cookiehouse.controller.dto.response.mission.ReadAllMissionCompleteResponseDto;
import com.groom.cookiehouse.controller.dto.response.mission.ReadMissionCompleteResponseDto;
import com.groom.cookiehouse.domain.mission.Mission;
import com.groom.cookiehouse.domain.mission.MissionComplete;
import com.groom.cookiehouse.domain.user.User;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.model.BadRequestException;
import com.groom.cookiehouse.exception.model.NotFoundException;
import com.groom.cookiehouse.external.client.aws.S3Service;
import com.groom.cookiehouse.repository.MissionCompleteRepository;
import com.groom.cookiehouse.repository.MissionRepository;
import com.groom.cookiehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionCompleteService {

    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final MissionCompleteRepository missionCompleteRepository;

    private final S3Service s3Service;

    @Transactional
    public CreateMissionCompleteResponseDto createMissionComplete(MissionCompleteRequestDto requestDto, Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Mission> missions = missionRepository.findAllByDate(LocalDate.now());

        if (!missionCompleteRepository.existsByUserAndMission(user, missions.get(0))) {
            MissionComplete missionComplete = MissionComplete.builder()
                    .image(imageUrl)
                    .content(requestDto.getMissionCompleteContent())
                    .mission(missions.get(0))
                    .user(user)
                    .furnitureId(requestDto.getFurnitureId())
                    .build();
            missionCompleteRepository.save(missionComplete);
            return CreateMissionCompleteResponseDto.of(missionComplete.getId(), missionComplete.getCreatedAt(), missionComplete.getUpdatedAt());
        } else {
            throw new BadRequestException(ErrorCode.ALREADY_MISSION_COMPLETE, ErrorCode.ALREADY_MISSION_COMPLETE.getMessage());
        }

    }

    @Transactional
    public CreateMissionCompleteResponseDto updateMissionComplete(Long userId, Long missionCompleteId, MissionCompleteRequestDto requestDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        MissionComplete missionComplete = missionCompleteRepository.findById(missionCompleteId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION_COMPLETE_EXCEPTION, ErrorCode.NOT_FOUND_MISSION_COMPLETE_EXCEPTION.getMessage()));

        s3Service.deleteFile(missionComplete.getImage());
        String imageUrl = s3Service.uploadImage(requestDto.getMissionCompleteImage(), "mission_complete");

        missionComplete.update(imageUrl, requestDto.getMissionCompleteContent(), requestDto.getFurnitureId());
        return CreateMissionCompleteResponseDto.of(missionComplete.getId(), missionComplete.getCreatedAt(), missionComplete.getUpdatedAt());
    }

    public ReadMissionCompleteResponseDto findMissionComplete(Long missionCompleteId) {
        MissionComplete missionComplete = missionCompleteRepository.findById(missionCompleteId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION_COMPLETE_EXCEPTION, ErrorCode.NOT_FOUND_MISSION_COMPLETE_EXCEPTION.getMessage()));
        return ReadMissionCompleteResponseDto.of(
                missionComplete.getMission().getMessage(),
                missionComplete.getId(),
                missionComplete.getImage(),
                missionComplete.getContent(),
                missionComplete.getMission().getDate(),
                missionComplete.getFurnitureId()
        );
    }

    public ReadAllMissionCompleteResponseDto findAllMissionComplete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<MissionComplete> missionCompletes = missionCompleteRepository.findAllByUser(user);
        List<ReadMissionCompleteResponseDto> readMissionCompleteResponseDtos = new ArrayList<>();
        for(MissionComplete missionComplete : missionCompletes) {
            readMissionCompleteResponseDtos.add(
                    ReadMissionCompleteResponseDto.of(
                            missionComplete.getMission().getMessage(),
                            missionComplete.getId(),
                            missionComplete.getImage(),
                            missionComplete.getContent(),
                            missionComplete.getMission().getDate(),
                            missionComplete.getFurnitureId()
                    )
            );
        }
        return ReadAllMissionCompleteResponseDto.of(user.getWallpaper(), readMissionCompleteResponseDtos);
    }

}
