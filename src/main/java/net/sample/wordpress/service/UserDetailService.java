package net.sample.wordpress.service;

import lombok.AllArgsConstructor;
import net.sample.wordpress.entity.CurrentUser;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailService.class);
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);

        User user = userRepository.findByUsername(username);
        if(user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException(username);
        }
        logger.info("User successfully loaded: {}", username);
        return new CurrentUser(user);
    }
}
