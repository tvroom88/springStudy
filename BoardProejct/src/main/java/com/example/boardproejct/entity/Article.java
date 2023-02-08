package com.example.boardproejct.entity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import jakarta.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private BoardUser author;

    @ManyToMany
    Set<BoardUser> voter;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    // Category - int : 1 이면 공지사항 2 이면 자유게시판
    @Column(columnDefinition = "integer default 1", nullable = false)
    private int category;

}







