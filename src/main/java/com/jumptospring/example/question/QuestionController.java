package com.jumptospring.example.question;

import com.jumptospring.example.answer.AnswerForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RequestMapping("/question")
@RequiredArgsConstructor //롬복이 제공하는 애너테이션으로 final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {

    private final QuestionService questionService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        //Model 객체는 자바 클래스와 템플릿 간의 연결고리 역할을 한다. Model 객체에 값을 담아두면 템플릿에서 그 값을 사용할 수 있다.
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @RequestMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    /**
     * QuestionController의 GetMapping으로 매핑한 메서드도 다음과 같이 변경해야 한다.
     * 왜냐하면 question_form.html 템플릿은 "질문 등록하기" 버튼을 통해 GET 방식으로 요청되더라도
     * th:object에 의해 QuestionForm 객체가 필요하기 때문이다.
     */
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    /**
     * @param questionForm
     * @param bindingResult 자동으로 QuestionForm의 Subject, Content에 바인딩 된다. (스프링 바인딩 기능)
     * @Vaild는 @NotEmpty, @Size 검증 BindingResult는 이로 인해 검증이 수행된 결과를 의미하는 객체
     */
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {

        //자동으로 QuestionForm의 subject, content에 바인딩된다. (스프링 프레임 워크의 바인딩 기능)
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list";
    }

}
