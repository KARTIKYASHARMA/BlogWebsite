package net.sample.wordpress.service;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import net.sample.wordpress.entity.Subscription;
import net.sample.wordpress.entity.SubscriptionType;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@NoArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getUsername());
        return savedUser;
    }

    public User findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    public String verifyAndGenerateToken(User user) {
        logger.debug("Authenticating user: {}", user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            User userFromDb = userRepository.findByUsername(user.getUsername());
            boolean matches = encoder.matches(user.getPassword(), userFromDb.getPassword());
            logger.debug("Password match for {}: {}", user.getUsername(), matches);

            if (authentication.isAuthenticated()) {
                logger.info("JWT issued for user: {}", userFromDb.getUsername());
                return jwtService.generateToken(userFromDb.getUsername(), userFromDb.getUserId());
            } else {
                logger.warn("Authentication failed for user: {}", user.getUsername());
            }
        } catch (Exception e) {
            logger.error("Authentication exception for user {}: {}", user.getUsername(), e.getMessage());
        }
        return null;
    }

    public List<User> findAllById(List<Long> userIds) {
        logger.debug("Fetching users with IDs: {}", userIds);
        return userRepository.findAllById(userIds);
    }

    public User findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            logger.info("Deleted user with ID: {}", userId);
        } else {
            logger.warn("Attempted to delete non-existent user ID: {}", userId);
        }
    }


}
