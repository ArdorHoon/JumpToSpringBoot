package com.jumptospring.example.question;


import com.jumptospring.example.answer.Answer;
import com.jumptospring.example.category.Category;
import com.jumptospring.example.error.DataNotFoundException;
import com.jumptospring.example.uesr.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;


    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        //순수 쿼리로 작성한 경우
        //return this.questionRepository.findAllByKeyword(kw, pageable);
        //Specification 사용한 경우
        return this.questionRepository.findAll(spec, pageable);
    }

//    public Page<Question> getListByCategory(int page, String kw, String category) {
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createDate"));
//        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        Specification<Question> spec = search(kw);
//        //순수 쿼리로 작성한 경우
//        //return this.questionRepository.findAllByKeyword(kw, pageable);
//        //Specification 사용한 경우
//        return this.questionRepository.findAll(spec, pageable);
//    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);

        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question Not Found");
        }

    }

    public List<Question> getQuestions(SiteUser user) {
        Optional<List<Question>> questions = this.questionRepository.findAllByAuthor(user);

        if (questions.isPresent()) {
            return questions.get();
        } else {
            throw new DataNotFoundException("question Not Found");
        }
    }

    public void create(String subject, String content, SiteUser author, Category category) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCategory(category);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(author);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder builder) {
                query.distinct(true); //중복제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return builder.or(builder.like(q.get("subject"), "%" + kw + "%"), //제목
                        builder.like(q.get("content"), "%" + kw + "%"), //내용
                        builder.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
                        builder.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        builder.like(u2.get("username"), "%" + kw + "%") // 답변 작성자
                );
            }

        };
    }

    public void incrementView(Question question) {
        question.setView(question.getView() + 1);
        this.questionRepository.save(question);
    }
}
