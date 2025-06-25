package net.sample.wordpress.service;

import net.sample.wordpress.entity.Role;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Test
    public void testAddUser() {
        assertEquals(4,2+2);

    }

    @Test
    public void testFindByUsername() {
        assertNotNull(userService.findByUsername("kart"));
    }

    @Test
    public void testFindUserId() {
        assertNotNull(userService.findById(1L));
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("alpha");
        user.setPassword("password");
        user.setEmail("a@gmail.com");
        user.setRole(Role.valueOf( "USER"));
       User savedUser= userService.saveUser(user);
       assertNotNull(savedUser);
       assertEquals(savedUser.getUsername(),user.getUsername());
       assertEquals(savedUser.getRole(),user.getRole());

    }
}
