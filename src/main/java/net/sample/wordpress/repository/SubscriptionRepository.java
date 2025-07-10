package net.sample.wordpress.repository;

import net.sample.wordpress.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserUserId(Long userId);

    List<Subscription> findByActiveTrue();

    List<Subscription> findByEndDateBefore(LocalDate date);

    List<Subscription> findByUserUsername(String username);
}
