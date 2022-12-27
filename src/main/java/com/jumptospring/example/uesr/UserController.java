package com.jumptospring.example.uesr;


import com.jumptospring.example.Message;
import com.jumptospring.example.answer.Answer;
import com.jumptospring.example.answer.AnswerService;
import com.jumptospring.example.question.Question;
import com.jumptospring.example.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final QuestionService questionService;

    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String getInfo(Model model, Principal principal) {
        SiteUser user = this.userService.getUser(principal.getName());
        List<Question> questionList = this.questionService.getQuestions(user);
        List<Answer> answerList = this.answerService.getListByAuthor(user);

        model.addAttribute("answers", answerList);
        model.addAttribute("questions", questionList);
        model.addAttribute("user", user);
        return "user_info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/password")
    public String modifyPassword(PasswordModifyForm passwordModifyForm) {
        return "modify_password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/password")
    public String modifyPassword(@Valid PasswordModifyForm passwordModifyForm, BindingResult bindingResult, Principal principal, Model model) {
        SiteUser user = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return "modify_password_form";
        }

        if (!this.userService.isSamePassword(user, passwordModifyForm.getBeforePassword())) {
            bindingResult.rejectValue("beforePassword", "notBeforePassword", "이전 비밀번호와 일치하지 않습니다. ");
            return "modify_password_form";
        }

        if (!passwordModifyForm.getPassword1().equals(passwordModifyForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "modify_password_form";
        }

        try {
            userService.modifyPassword(user, passwordModifyForm.getPassword1());
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("modifyPasswordFailed", e.getMessage());
            return "modify_password_form";
        }

        model.addAttribute("data", new Message("비밀번호 변경 되었습니다.", "/user/info"));
        return "message";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
}
