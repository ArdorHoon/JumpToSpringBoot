package com.jumptospring.example.answer;

import com.jumptospring.example.question.Question;
import com.jumptospring.example.question.QuestionService;
import com.jumptospring.example.uesr.SiteUser;
import com.jumptospring.example.uesr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid AnswerForm answerForm,
                               BindingResult bindingResult, Principal principal) {

        Question question = this.questionService.getQuestion(id);

        //principal은 스프링 시큐리티가 제공
        SiteUser siteUser = this.userService.getUser(principal.getName());

        // TODO: 답변을 저장한다.
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        this.answerService.create(question, answerForm.getContent(),siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
