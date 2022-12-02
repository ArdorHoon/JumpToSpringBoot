package com.jumptospring.example.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@RequiredArgsConstructor //롬복이 제공하는 애너테이션으로 final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {

    private final QuestionRepository questionRepository;

    @RequestMapping("/question/list")
    public String list(Model model) {
        List<Question> questionList = this.questionRepository.findAll();
        //Model 객체는 자바 클래스와 템플릿 간의 연결고리 역할을 한다. Model 객체에 값을 담아두면 템플릿에서 그 값을 사용할 수 있다.
        model.addAttribute("questionList", questionList);
        return "question_list";
    }

}