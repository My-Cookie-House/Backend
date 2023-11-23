package com.groom.cookiehouse.controller.dto.response.mission;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadMissionCompleteResponseDto {

    private String missionMessage;
    private Long missionCompleteId;
    private String missionCompleteImage;
    private String missionCompleteContent;
    private LocalDate missionCompleteDate;
    private Long missionCompleteFurnitureId;

    public static ReadMissionCompleteResponseDto of(
            String missionMessage,
            Long missionCompleteId,
            String missionCompleteImage,
            String missionCompleteContent,
            LocalDate missionCompleteDate,
            Long missionCompleteFurnitureId
    ) {
        return new ReadMissionCompleteResponseDto(
                missionMessage,
                missionCompleteId,
                missionCompleteImage,
                missionCompleteContent,
                missionCompleteDate,
                missionCompleteFurnitureId
        );
    }

}
