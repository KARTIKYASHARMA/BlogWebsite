package net.sample.wordpress.service;

import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElse(null);
    }

}
