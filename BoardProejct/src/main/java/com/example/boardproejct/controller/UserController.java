package com.example.boardproejct.controller;

import com.example.boardproejct.form.UserForm;
import com.example.boardproejct.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final ArticleService articleService;

    //회원가입
    @GetMapping("/signup")
    public String signup(UserForm userForm) {
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        //패스워드가 일치 하지 않을경우
        if (!userForm.getPassword1().equals(userForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "user/signup";
        }

        try {
            articleService.createUser(userForm.getUsername(), userForm.getPassword1(),
                    userForm.getEmail());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "user/signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "user/signup";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }
}