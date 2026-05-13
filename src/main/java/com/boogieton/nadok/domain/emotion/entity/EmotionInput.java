package com.boogieton.nadok.domain.emotion.entity;

import com.boogieton.nadok.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emotion_input")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionInput extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inputId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String inputText;

    @Column(nullable = false)
    private String emotionTag;

    @Column(nullable = false)
    private String comfortMethod;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
}
