package com.groom.cookiehouse.controller.dto.response.mission;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadAllMissionCompleteResponseDto {

    private Long wallpaperId;
    private List<ReadMissionCompleteResponseDto> completedMissions;

    public static ReadAllMissionCompleteResponseDto of(Long wallpaperId, List<ReadMissionCompleteResponseDto> completedMissions) {
        return new ReadAllMissionCompleteResponseDto(wallpaperId, completedMissions);
    }

}
