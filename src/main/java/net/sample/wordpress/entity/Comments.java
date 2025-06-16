package net.sample.wordpress.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String comment;

    @ManyToOne
    @JoinColumn(name="blog_id",referencedColumnName ="blogId")
    private Blog blog;

    @ManyToOne
    @JoinColumn(name="user_Id",referencedColumnName = "userId")
    private User user;

}
