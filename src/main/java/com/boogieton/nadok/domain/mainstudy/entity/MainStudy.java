package com.boogieton.nadok.domain.mainstudy.entity;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import com.boogieton.nadok.domain.memo.entity.Memo;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "main_study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MainStudy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mainId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus readingStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "mainStudy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Memo> memos = new ArrayList<>();

    public void update(ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate) {
        if (readingStatus != null) this.readingStatus = readingStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
