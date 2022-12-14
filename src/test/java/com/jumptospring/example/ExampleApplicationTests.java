package com.jumptospring.example;

import com.jumptospring.example.answer.Answer;
import com.jumptospring.example.answer.AnswerService;
import com.jumptospring.example.error.DataNotFoundException;
import com.jumptospring.example.question.Question;
import com.jumptospring.example.question.QuestionService;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class ExampleApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;


    @DisplayName("잘못된 id로 Question 조회 테스트 Sample")
    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void createQuestionWithoutSubjectTest(Integer id) {
        assertThatCode(() -> questionService.getQuestion(id)).doesNotThrowAnyException();
    }

}
