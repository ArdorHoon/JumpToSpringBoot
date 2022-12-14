package com.jumptospring.example.answer;

import com.jumptospring.example.error.DataNotFoundException;
import com.jumptospring.example.question.Question;
import com.jumptospring.example.uesr.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    public static final String RECENT_ORDER = "recent";
    public static final String RECOMMEND_ORDER = "recommend";
    private final AnswerRepository answerRepository;

    public Page<Answer> getList(int page, Question question, String so) {
        List<Sort.Order> sorts = new ArrayList<>();

        if (RECOMMEND_ORDER.equals(so)) {
            sorts.add(Sort.Order.desc("voter"));
        } else {
            sorts.add(Sort.Order.desc("createDate"));
        }
        Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));
        return this.answerRepository.findAllByQuestion(question, pageable);
    }

    public List<Answer> getListByAuthor(SiteUser user){
        Optional<List<Answer>> answers = this.answerRepository.findAllByAuthor(user);

        if(answers.isPresent()){
            return answers.get();
        }else{
            throw new DataNotFoundException("answers not found");
        }

    }

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);

        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }


    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
