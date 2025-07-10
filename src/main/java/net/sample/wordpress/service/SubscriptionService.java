package net.sample.wordpress.service;

import lombok.RequiredArgsConstructor;
import net.sample.wordpress.entity.Subscription;
import net.sample.wordpress.entity.SubscriptionType;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;

    // ✅ Create new subscription
    public Subscription createSubscription(User user, SubscriptionType type, int durationInDays) {
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setType(type);
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusDays(durationInDays));
        sub.setActive(true);
        return subscriptionRepository.save(sub);
    }

    // ✅ Check if user has any active subscription
    public boolean hasActiveSubscription(User user) {
        return subscriptionRepository.findByUserUserId(user.getUserId()).stream()
                .anyMatch(s -> s.isActive() && s.getEndDate().isAfter(LocalDate.now()));
    }

    // ✅ Get remaining days in subscription
    public long daysRemaining(User user) {
        return subscriptionRepository.findByUserUserId(user.getUserId()).stream()
                .filter(s -> s.isActive() && s.getEndDate().isAfter(LocalDate.now()))
                .map(s -> LocalDate.now().until(s.getEndDate()).getDays())
                .findFirst().orElse(0);
    }

    // ✅ Scheduled cleanup
    @Scheduled(fixedDelay = 604800000) // every 7 days
    public void deactivateExpiredSubscriptions() {
        List<Subscription> expiredSubs = subscriptionRepository.findByEndDateBefore(LocalDate.now());
        for (Subscription sub : expiredSubs) {
            if (sub.isActive()) {
                sub.setActive(false);
                subscriptionRepository.save(sub);
                logger.info("Deactivated expired subscription for user: {}", sub.getUser().getUsername());
            }
        }
    }
}
