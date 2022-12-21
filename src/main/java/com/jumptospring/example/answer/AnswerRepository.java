package com.jumptospring.example.answer;

import com.jumptospring.example.question.Question;
import com.jumptospring.example.uesr.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findAllByQuestion(Question question, Pageable pageable);

    Optional<List<Answer>> findAllByAuthor(SiteUser user);

    Page<Answer> findAllByQuestion(Question question, Specification<Answer> spec, Pageable pageable);

}
