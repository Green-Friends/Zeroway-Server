package com.zeroway.community.entity;

import com.zeroway.common.BaseEntity;
import com.zeroway.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1000)
    private String content;

    @Builder
    public Post(User user, String content) {
        this.user = user;
        this.content = content;
    }
}
