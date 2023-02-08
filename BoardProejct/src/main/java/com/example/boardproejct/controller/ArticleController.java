package com.example.boardproejct.controller;

import com.example.boardproejct.entity.Article;
import com.example.boardproejct.entity.BoardUser;
import com.example.boardproejct.form.ArticleForm;
import com.example.boardproejct.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j // 로깅을 위한 롬복 어노테이션
@RequiredArgsConstructor
@Controller
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    // 게시판, 키워드, 페이지별 글을 보여주는 부분
    @GetMapping("/list")
    public String list(Model model, @RequestParam Map<String, String> params) {
        // 기본값을 공지사항, 첫번째 페이지, "" searchKeyword로 세팅
        int boardId = 1;
        int page = 1;
        String searchKeyword = "";

        if (params.containsKey("page")) {
            page = Integer.parseInt(params.get("page"));
        }
        if (params.containsKey("boardId")) {
            boardId = Integer.parseInt(params.get("boardId"));
        }
        if (params.containsKey("search")) {
            searchKeyword = params.get("search");
        }

        // Keyword에 맞는 Article data 가져오는 부분
        List<Article> articles = articleService.getArticleByKeyword(searchKeyword);

        // boardId에 따라 공지사항인지 일반게시판글인지 구분
        int finalBoardId = boardId;
        articles = articles.stream()
                .filter((article) -> article.getCategory() == finalBoardId).toList();


        // paging 처리 (0~9, 10~19, 20~29 ....)
        int startIdx = (page - 1) * 10;

        //22
        //마지막 페이지일 경우 (1!=3->10, 2!=3 20, 22
        int endIdx = page != (articles.size() / 10) + 1 ? (page) * 10 : articles.size();

        List<Integer> pageNum = new ArrayList<>();
        for (int i = 1; i <= (articles.size() - 1) / 10 + 1; i++) {
            pageNum.add(i);
        }

        // 페이지 범위를 넘어간다면 redirect
        if (page < 0 || page > (articles.size() / 10) + 1) {
            return "redirect:/article/list?boardId=1";
        } else {

            List<Article> curArticles = articles.subList(startIdx, endIdx);
            model.addAttribute("boardId", boardId);
            model.addAttribute("articles", curArticles);
            model.addAttribute("pageNum", pageNum);
            model.addAttribute("search", searchKeyword);

            return "article/list";
        }

    }


    // 게시글 상세 페이지 부분
    //TODO - 글쓴이인지 아닌지 비교 해주는 부분
    @GetMapping(value = "/detail")
    public String detail(Model model, @RequestParam Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Article article = this.articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "article/detail";
    }

    //TODO - 글쓴이인지 아닌지 비교 해주는 부분
    @GetMapping("/modify")
    public String articleModify(ArticleForm articleForm, Principal principal, @RequestParam Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Article article = this.articleService.getArticleById(id);
        articleForm.setId(article.getId());
        articleForm.setCategory(article.getCategory());
        articleForm.setTitle(article.getTitle());
        articleForm.setContent(article.getContent());
        return "article/edit";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/doModify")
    public String articleModify(@Valid ArticleForm articleForm, BindingResult bindingResult,
                                Principal principal) {
        Article article = this.articleService.getArticleById(articleForm.getId());
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.articleService.modify(article, articleForm.getTitle(), articleForm.getContent());
        return String.format("redirect:/article/detail?id=%s", articleForm.getId());
    }


    // Create - article를 새로 작성하는 부분
    @GetMapping("/write")
    public String writeArticle(ArticleForm articleForm, Model model, @RequestParam Map<String, String> params) {

        int boardId = Integer.parseInt(params.get("boardId"));
        articleForm.setCategory(boardId);
        model.addAttribute("boardId", boardId);
        return "article/write";
    }

    // Create - article를 작성 완료시 저장해주고 /usr/home/main으로 redirect
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/doWrite")
    public String articleCreate(@Valid ArticleForm articleForm, BindingResult bindingResult,
                                Principal principal) {

        if (bindingResult.hasErrors()) {
            return "article/write";
        }

        BoardUser boardUser = this.articleService.getUser(principal.getName());

        //User가 admin이 아닌데 공지사항 저장하려 할때는 저장 안되게
        if (boardUser.getId() != 1 && articleForm.getCategory() == 1) {
            bindingResult.reject("writeFailed", "공지사항 글쓰기는 admin유저만 가능합니다");
        } else {
            this.articleService.create(articleForm.getTitle(), articleForm.getContent(), articleForm.getCategory(), boardUser);
        }

        return String.format("redirect:/article/list?boardId=%s", articleForm.getCategory()); // 질문 저장후 질문목록으로 이동
    }

    @GetMapping("/delete")
    public String articleDelete(Principal principal, @RequestParam Map<String, String> params) {
        int boardId = Integer.parseInt(params.get("id"));
        Article article = this.articleService.getArticleById(boardId);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.articleService.delete(boardId);
        return String.format("redirect:/article/list?boardId=%s", article.getCategory()); // 질문 저장후 질문목록으로 이동

    }

}













