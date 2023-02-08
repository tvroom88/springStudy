package com.example.boardproejct.service;

import com.example.boardproejct.entity.Article;
import com.example.boardproejct.entity.BoardUser;
import com.example.boardproejct.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(String subject, String content, int category, BoardUser author) {
        Article article = new Article();
        article.setTitle(subject);
        article.setContent(content);
        article.setCreateDate(LocalDateTime.now());
        article.setModifyDate(LocalDateTime.now());
        article.setCategory(category);
        article.setAuthor(author);

        this.articleRepository.save(article);
    }

    // 회원 등록하는 부분
    public void createUser(String username, String password, String email) {
        BoardUser user = new BoardUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        this.articleRepository.save(user);
    }


    // 모든 게시글 가져오는 메소드
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        Collections.sort(articles, (Article a, Article b) -> b.getCreateDate().compareTo(a.getCreateDate())); //날짜 비교
        return articles;
    }

    public List<Article> getArticleByCategory(int category) {
        List<Article> articles = articleRepository.findArticleByCategory(category);
        Collections.sort(articles, (Article a, Article b) -> b.getCreateDate().compareTo(a.getCreateDate())); //날짜 비교
        return articles;
    }

    public Article getArticleById(int id) {
        return articleRepository.findArticleById(id);
    }


    public BoardUser getUser(String username) {
        BoardUser boardUser = this.articleRepository.findByusername(username);
        return boardUser;
    }


    //keyword로 찾기
    public List<Article> getArticleByKeyword(String keyword) {
        List<Article> articles = articleRepository.findArticleByKeyword(keyword);
        Collections.sort(articles, (Article a, Article b) -> b.getCreateDate().compareTo(a.getCreateDate())); //날짜 비교
        return articles;
    }

    public void modify(Article article, String subject, String content) {
        article.setTitle(subject);
        article.setContent(content);
        article.setModifyDate(LocalDateTime.now());
        this.articleRepository.updateArticle(article);
    }

    public void delete(int id) {
        this.articleRepository.deleteArticle(id);
    }

}
