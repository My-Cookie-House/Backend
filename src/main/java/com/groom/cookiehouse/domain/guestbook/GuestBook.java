package com.groom.cookiehouse.domain.guestbook;

import com.groom.cookiehouse.domain.BaseEntity;
import com.groom.cookiehouse.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "GUEST_BOOK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestBook extends BaseEntity {

    @Column(nullable = false)
    private String author;

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long ornamentId;

    @Builder
    public GuestBook(String author, String content, User user, Long ornamentId) {
        this.author = author;
        this.content = content;
        this.user = user;
        this.ornamentId = ornamentId;
    }
}
