package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String loginUser(Model model, @ModelAttribute("user") User user) {
        logger.debug("GET /login page accessed");
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user,
                            HttpServletResponse response,
                            Model model) {
        logger.info("Login attempt for username: {}", user.getUsername());
        String token = userService.verifyAndGenerateToken(user);

        if (token != null) {
            logger.info("Login successful for user: {}", user.getUsername());
            // Store token in a secure, HttpOnly cookie
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); // Not accessible via JavaScript
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60); // 10 minutes
            response.addCookie(cookie);

            logger.debug("JWT cookie set for user: {}", user.getUsername());
            return "redirect:/user/home-page/show-all-blogs";
        } else {
            logger.warn("Login failed for user: {}", user.getUsername());
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
