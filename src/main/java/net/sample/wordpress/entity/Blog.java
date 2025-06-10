package net.sample.wordpress.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long blogId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    @ToString.Exclude
    private User user;


}
