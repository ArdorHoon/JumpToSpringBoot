package com.jumptospring.example.comment;

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
    public String createCommentInQuestion(Model model, @PathVariable("id") Integer id,
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
}
