package com.jumptospring.example.question;

import com.jumptospring.example.answer.Answer;
import com.jumptospring.example.comment.Comment;
import com.jumptospring.example.uesr.SiteUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@DynamicInsert
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private SiteUser author;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @ManyToMany
    Set<SiteUser> voter;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;
}
