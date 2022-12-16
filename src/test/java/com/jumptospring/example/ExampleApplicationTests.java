package com.jumptospring.example;

import com.jumptospring.example.error.DataNotFoundException;
import com.jumptospring.example.question.Question;
import com.jumptospring.example.question.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class ExampleApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 50; i++) {
            String subject = String.format("[테스트] 임시 제목 입니다: [%03d]", i);
            String content = "내용 없음";
            this.questionService.create(subject, content, null);
        }
    }

    @DisplayName("잘못된 id로 Question 조회 테스트 Sample")
    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void createQuestionWithoutSubjectTest(Integer id) {
        assertThatCode(() -> questionService.getQuestion(id)).doesNotThrowAnyException();
    }

}
