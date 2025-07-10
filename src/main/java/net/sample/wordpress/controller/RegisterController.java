package net.sample.wordpress.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final UserService userService;

    @GetMapping()
    public String showRegisterForm(Model model) {
        logger.debug("GET /register - Registration form displayed");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping()
    public String registerUser(@Valid User user, BindingResult result, Model model) {
        logger.info("Registration attempt for username: {}", user.getUsername());

        if (result.hasErrors()) {
            logger.warn("Binding validation failed for user: {}", user.getUsername());
            return "register";
        }

        try {
            User isRegistered = userService.saveUser(user);

            if (isRegistered == null) {
                logger.error("User registration failed (null returned): {}", user.getUsername());
                result.reject("registration.failed", "Registration failed. Please try again.");
                return "register";
            }

            logger.info("User successfully registered: {}", user.getUsername());
            return "redirect:/login";

        } catch (IllegalArgumentException ex) {
            logger.error("Validation error during registration: {}", ex.getMessage());

            // Map error message to the appropriate field
            if (ex.getMessage().toLowerCase().contains("username")) {
                result.rejectValue("username", null, ex.getMessage());
            } else if (ex.getMessage().toLowerCase().contains("email")) {
                result.rejectValue("email", null, ex.getMessage());
            } else {
                result.reject("registration.failed", ex.getMessage());
            }

            return "register";

        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            for (ConstraintViolation<?> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                logger.error("Constraint violation on field '{}': {}", field, message);
                result.rejectValue(field, null, message);
            }
            return "register";
        }
    }

}
