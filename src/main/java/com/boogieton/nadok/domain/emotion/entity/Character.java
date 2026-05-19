package com.boogieton.nadok.domain.emotion.entity;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "characters")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Character extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long characterId;

    @Column(nullable = false)
    private String characterName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bookQuote;

    @Column(nullable = false, name = "character_img_url")
    private String characterImgUrl;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String methodReason;

}