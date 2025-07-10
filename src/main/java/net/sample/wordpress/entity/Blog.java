package net.sample.wordpress.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long blogId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int likeCount;

    @Column(nullable = false)
    private int views = 0;



    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Likes> likes = new ArrayList<>();


    // Path to the uploaded image file
    private String imagePath;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private java.util.Date createdDate;




    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    @ToString.Exclude
    private User user;

    public void addLike(Likes like) {
        likes.add(like);
        like.setBlog(this);
    }

    public void addComment(Comments comment) {
        comments.add(comment);
        comment.setBlog(this);
    }




}
