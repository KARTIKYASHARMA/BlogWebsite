package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/login")
    public String loginUser(Model model, @ModelAttribute("user") User user)
    {
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user,
                            HttpServletResponse response,
                            Model model) {
        String token = userService.verifyAndGenerateToken(user);

        if (token != null) {
            // Store token in a secure, HttpOnly cookie
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); // Not accessible via JavaScript
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60); // 10 minutes
            response.addCookie(cookie);

            return "redirect:/user/home-page/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
//
//    @GetMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        Cookie cookie = new Cookie("jwt", "");
//        cookie.setMaxAge(0); // Delete the cookie
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        return "redirect:/login";
//    }
}
