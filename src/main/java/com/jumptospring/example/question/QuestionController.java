package com.jumptospring.example.question;

import com.jumptospring.example.answer.AnswerForm;
import com.jumptospring.example.uesr.SiteUser;
import com.jumptospring.example.uesr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;


@RequestMapping("/question")
@RequiredArgsConstructor //롬복이 제공하는 애너테이션으로 final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw);
        //Model 객체는 자바 클래스와 템플릿 간의 연결고리 역할을 한다. Model 객체에 값을 담아두면 템플릿에서 그 값을 사용할 수 있다.
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @RequestMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,  Principal principal) {
        Question question = this.questionService.getQuestion(id);
        //조회 수 증가 (비로그인 & 작성자 제외)
        if(principal != null && !question.getAuthor().getUsername().equals(principal.getName())) {
            this.questionService.incrementView(question);
        }
        model.addAttribute("question", question);
        return "question_detail";
    }

    /**
     * QuestionController의 GetMapping으로 매핑한 메서드도 다음과 같이 변경해야 한다.
     * 왜냐하면 question_form.html 템플릿은 "질문 등록하기" 버튼을 통해 GET 방식으로 요청되더라도
     * th:object에 의해 QuestionForm 객체가 필요하기 때문이다.
     */
    @PreAuthorize("isAuthenticated()") //로그아웃 상태에서는 로그인 페이지로 간다.
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    /**
     * @param questionForm
     * @param bindingResult 자동으로 QuestionForm의 Subject, Content에 바인딩 된다. (스프링 바인딩 기능)
     * @Vaild는 @NotEmpty, @Size 검증 BindingResult는 이로 인해 검증이 수행된 결과를 의미하는 객체
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {

        //자동으로 QuestionForm의 subject, content에 바인딩된다. (스프링 프레임 워크의 바인딩 기능)
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        SiteUser author = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), author);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()") //로그아웃 상태에서는 로그인 페이지로 간다.
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            SiteUser siteUser = this.userService.getUser(principal.getName());
            this.questionService.vote(question, siteUser);
        } else {
            /**
             * alert 창 후 이동하지 않는 문제가 있음
             */
            // ScriptUtils.alertAndMovePage(response, "작성자는 추천이 불가합니다.", String.format("redirect:/question/detail/%s", id));
        }

        return String.format("redirect:/question/detail/%s", id);
    }

}
