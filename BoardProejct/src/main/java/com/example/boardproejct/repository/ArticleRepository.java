package com.example.boardproejct.repository;

import com.example.boardproejct.entity.Article;
import com.example.boardproejct.entity.BoardUser;

import java.util.List;

public interface ArticleRepository {

    //Create
    void save(Article article);
    void save(BoardUser boardUser);

    BoardUser findByusername(String username);

    //Read
    List<Article> findAll();

    //게시판별 게시글 가져오기
    List<Article> findArticleByCategory(int category);

    Article findArticleById(int id);


    //Search
    List<Article> findArticleByKeyword(String subject);

    //Update
    void updateArticle(Article article);

    //Delete
    void deleteArticle(int id);



}
