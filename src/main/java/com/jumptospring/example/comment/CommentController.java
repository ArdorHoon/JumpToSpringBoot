package com.jumptospring.example.comment;

import com.jumptospring.example.answer.Answer;
import com.jumptospring.example.answer.AnswerService;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String createAboutQuestion(Model model, @PathVariable("id") Integer id,
                                      @Valid CommentForm commentForm,
                                      BindingResult bindingResult, Principal principal) {

        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        Comment comment = this.commentService.create(question, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createAboutAnswer(Model model, @PathVariable("id") Integer id,
                                    @RequestParam(value = "so", defaultValue = "recent") String so,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @Valid CommentForm commentForm,
                                    BindingResult bindingResult, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Question question = answer.getQuestion();

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        Comment comment = this.commentService.create(answer, commentForm.getContent(), siteUser);
        model.addAttribute("so", so);
        return String.format("redirect:/question/detail/%s?page=%s&so=%s#answer_%s", comment.getAnswer().getQuestion().getId(), page, so, comment.getAnswer().getId());
    }
}
