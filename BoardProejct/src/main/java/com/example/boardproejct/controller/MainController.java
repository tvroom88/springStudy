package com.example.boardproejct.controller;

import com.example.boardproejct.entity.Article;
import com.example.boardproejct.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {
    @GetMapping("/")
    public String root() {
        return "redirect:/home/main";
    }

    private final ArticleService articleService;

    //메인 페이지. 공지사항, 자유게시판 글이 10개씩 보여줌.
    @GetMapping("/home/main")
    public String home(Model model) {

        //공지사항 게시물 10개 가져오기
        List<Article> articleType1 = articleService.getArticleByCategory(1);
        int min1 = Math.min(articleType1.size(), 10); //게시물이 10개 보다 적을경우 체크
        articleType1 = articleType1.subList(0, min1);

        //자유 게시물 10개 가져요기
        List<Article> articleType2 = articleService.getArticleByCategory(2);
        int min2 = Math.min(articleType2.size(), 10); //게시물이 10개 보다 적을경우 체크
        articleType2 = articleType2.subList(0, min2);

        model.addAttribute("articles1", articleType1);
        model.addAttribute("articles2", articleType2);

        return "article/home";
    }
}
