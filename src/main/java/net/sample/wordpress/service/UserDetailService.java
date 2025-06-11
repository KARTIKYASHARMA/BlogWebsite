package net.sample.wordpress.service;

import lombok.AllArgsConstructor;
import net.sample.wordpress.entity.CurrentUser;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException(username);
        }
        return new CurrentUser(user);
    }
}
