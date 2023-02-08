package com.example.boardproejct.form;

import com.example.boardproejct.entity.BoardUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleForm {

    private Integer id;

    private int category;

    private BoardUser author;

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String title;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

}