package com.jumptospring.example.answer;

import com.jumptospring.example.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findAllByQuestion(Question question, Pageable pageable);
    Page<Answer> findAllByQuestion(Question question, Specification<Answer> spec, Pageable pageable);

}
