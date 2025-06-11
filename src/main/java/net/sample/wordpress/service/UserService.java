package net.sample.wordpress.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder encoder ;

    @Autowired
    private JwtService jwtService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
       return userRepository.save(user);

    }

    public User findById(Long id) {

        return userRepository.findById(id).orElse(null);
    }

    public String verifyAndGenerateToken(User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            User userFromDb = userRepository.findByUsername(user.getUsername());
            System.out.println("Raw: " + user.getPassword());
            System.out.println("Hashed: " + userFromDb.getPassword());
            System.out.println("Matches: " + encoder.matches(user.getPassword(), userFromDb.getPassword()));

            boolean matches = encoder.matches(user.getPassword(), userFromDb.getPassword());
            System.out.println("Password matches: " + matches);

            if (authentication.isAuthenticated()) {
                System.out.println("token:"+jwtService.generateToken(userFromDb.getUsername(),userFromDb.getUserId()));
                return jwtService.generateToken(userFromDb.getUsername(),userFromDb.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
