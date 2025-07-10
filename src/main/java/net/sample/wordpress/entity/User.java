package net.sample.wordpress.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[\\S]{8,}$",
            message = "Password must be at least 8 characters long and include an uppercase letter, lowercase letter, number, and special character."
    )
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @ToString.Exclude
    private List<Blog> blogs = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comments> comments = new ArrayList<>();


    public void addBlog(Blog blog) {
        blogs.add(blog);
        blog.setUser(this);
    }
    //Utility Methods for Bi-Directional Sync (Recommended)
    public void addLike(Likes like) {
        likes.add(like);
        like.setUser(this);
    }

    public void addComment(Comments comment) {
        comments.add(comment);
        comment.setUser(this);
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Subscription> subscriptions = new ArrayList<>();

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.setUser(this);
    }
    public boolean hasActiveSubscription() {
        return subscriptions.stream()
                .anyMatch(s -> s.isActive() && s.getEndDate().isAfter(LocalDate.now()));
    }
    public long daysRemainingInSubscription() {
        return subscriptions.stream()
                .filter(s -> s.isActive() && s.getEndDate().isAfter(LocalDate.now()))
                .map(s -> LocalDate.now().until(s.getEndDate()).getDays())
                .findFirst().orElse(0);
    }



}
