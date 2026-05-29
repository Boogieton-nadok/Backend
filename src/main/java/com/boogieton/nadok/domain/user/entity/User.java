package com.boogieton.nadok.domain.user.entity;

import com.boogieton.nadok.domain.chat.entity.ChatRoom;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import com.boogieton.nadok.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Gender gender;

    @Column(nullable = true)
    private Date birthday;

    @Column(nullable = true)
    private String profileImgUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "mainId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MainStudy> mainStudies = new ArrayList<>();

    public void updateProfile(String nickname, String password, Gender gender, Date birthday) {
        if (nickname != null) this.nickname = nickname;
        if (password != null) this.password = password;
        if (gender != null) this.gender = gender;
        if (birthday != null) this.birthday = birthday;
    }


    public void updateProfileImg(String profileImgUrl) {
        if (profileImgUrl != null) {
            this.profileImgUrl = profileImgUrl;
        }
    }
}
