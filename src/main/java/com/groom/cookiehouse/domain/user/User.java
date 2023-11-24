package com.groom.cookiehouse.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.groom.cookiehouse.domain.BaseEntity;
import com.groom.cookiehouse.domain.guestbook.GuestBook;
import com.groom.cookiehouse.domain.mission.MissionComplete;
import lombok.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@DynamicInsert // null값이 아닌 필드만을 대상으로 SQL INSERT 문을 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true)
    private String houseName;

    @Column(nullable = true)
    private Long cookieOne;

    @Column(nullable = true)
    private Long cookieTwo;

    @Column(nullable = true)
    private Long icing;

    @Column(nullable = true)
    private Long wallpaper;

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GuestBook> guestBookList;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MissionComplete> missionCompleteList;

    @Builder
    public User(String userName, String socialId, SocialType socialType) {
        this.userName = userName;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public void updateHouse(String houseName, Long cookieOne, Long cookieTwo, Long icing, Long wallpaper) {
        this.houseName = houseName;
        this.cookieOne = cookieOne;
        this.cookieTwo = cookieTwo;
        this.icing = icing;
        this.wallpaper = wallpaper;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}