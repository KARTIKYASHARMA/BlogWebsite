package net.sample.wordpress.controller;

import lombok.AllArgsConstructor;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final UserService userService;

    @GetMapping()
    public String showRegisterationForm(Model model) {
        logger.debug("GET /register - Registration form displayed");
        model.addAttribute("user", new User());
        return "register";

    }

    @PostMapping()
    public String registerUser(User user, Model model) {
        logger.info("Registration attempt for username: {}", user.getUsername());

        User isRegistered= userService.saveUser(user);
        if(isRegistered == null) {
            logger.error("Failed to register user: {}", user.getUsername());
            throw new UsernameNotFoundException("User not found");

        }
        logger.info("User successfully registered: {}", user.getUsername());
        model.addAttribute("user", user);
        return "redirect:/login";
    }




}
