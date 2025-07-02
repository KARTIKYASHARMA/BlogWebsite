package net.sample.wordpress.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long userId;

    String username;
    String password;
    String email;
    @Enumerated(EnumType.STRING)
    Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Blog> blogs=new ArrayList<>();

    public void addBlog(Blog blog) {
        blogs.add(blog);
        blog.setUser(this);
    }

}
