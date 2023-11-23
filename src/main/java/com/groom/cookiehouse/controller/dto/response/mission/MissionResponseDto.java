package com.groom.cookiehouse.controller.dto.response.mission;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionResponseDto {

    private Long missionId;
    private LocalDate missionDate;
    private String missionMessage;
    private Long missionCompleteId;

    public static MissionResponseDto of(Long missionId, LocalDate missionDate, String missionMessage, Long missionCompleteId) {
        return new MissionResponseDto(missionId, missionDate, missionMessage, missionCompleteId);
    }

}
