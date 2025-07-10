package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.SubscriptionType;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.SubscriptionService;
import net.sample.wordpress.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/home-page")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private JwtService jwtService;





    @GetMapping("/")
    public String homePage(Model model,HttpServletRequest request) {
        logger.debug("GET /user/home-page/ - Loading homepage");
        List<Blog> blogs = blogService.getAllBlogs();
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) return "redirect:/login";


        Long loggedInUserId=jwtService.extractUserId(jwtToken);
        User user = userService.findById(loggedInUserId); // Get from JWT or session

        boolean hasActiveSub = subscriptionService.hasActiveSubscription(user);
        long daysLeft = subscriptionService.daysRemaining(user);

        model.addAttribute("hasActiveSub", hasActiveSub);
        model.addAttribute("daysLeft", daysLeft);

        logger.info("Loaded {} blogs for homepage", blogs.size());
        model.addAttribute("blogs", blogs);
        return "home-page";
    }

    @GetMapping("/register-user")
    public String registerUser(Model model) {
        logger.debug("GET /register-user - Displaying user registration form");
        User user =new User();
        model.addAttribute("user", user);
        return "register-user";
    }
    @PostMapping("/user-saved")
    public String addUserToDatabase(Model model, @ModelAttribute User user)
    {
        logger.info("Saving new user: {}", user.getUsername());
        userService.saveUser(user);
        model.addAttribute("user", user);
        logger.info("User {} saved successfully", user.getUsername());
        return "user-saved";

    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        logger.info("Logging user out. Deleting JWT cookie.");
        // Delete the JWT cookie
        System.out.println("cookie deleted");
        Cookie cookie = new Cookie("jwt", null); // Same name as your token
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // only if you're using HTTPS
        cookie.setPath("/"); // match the path of the original cookie
        cookie.setMaxAge(0); // delete the cookie
        response.addCookie(cookie);

        // Redirect to login
        logger.debug("JWT cookie deleted. Redirecting to login.");
        return "redirect:/login";
    }

    @Transactional
    @GetMapping("/delete-user-by-id")
    public String deleteUserById(@RequestParam("id") Long userId, Model model) {
        logger.info("Request received to delete user with ID: {}", userId);
        try {
            userService.deleteUser(userId);
            logger.info("User with ID {} deleted successfully.", userId);
            model.addAttribute("message", "User deleted successfully.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Failed to delete user with ID {}: {}", userId, e.getMessage());
            model.addAttribute("error", "Error deleting user.");
            return "error-page";
        }
    }
    @GetMapping("/subscribe")
    public String showSubscribePage(HttpServletRequest request, Model model) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) return "redirect:/login";

        Long userId = jwtService.extractUserId(jwtToken);
        model.addAttribute("userId", userId);
        model.addAttribute("subscriptionTypes", SubscriptionType.values());

        return "subscribe"; // Thymeleaf template
    }
    @PostMapping("/subscribe")
    public String subscribeUser(@RequestParam("userId") Long userId,
                                @RequestParam("type") SubscriptionType type,
                                Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error-page";
        }

        // ✅ Check if user already has an active subscription
        if (subscriptionService.hasActiveSubscription(user)) {
            model.addAttribute("error", "You already have an active subscription.");
            model.addAttribute("userId", userId);
            model.addAttribute("subscriptionTypes", SubscriptionType.values());
            return "subscribe"; // Show error on same page
        }

        // ✅ Proceed only if no active subscription
        subscriptionService.createSubscription(user, type, 7); // 7-day duration
        return "redirect:/user/home-page/show-all-blogs?subscribed=true";
    }


    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }



}
