package net.sample.wordpress.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
