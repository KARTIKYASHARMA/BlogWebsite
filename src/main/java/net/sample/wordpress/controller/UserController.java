package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user/home-page")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;



    @GetMapping("/")
    public String homePage(Model model) {
        List<Blog> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
        return "home-page";
    }

    @GetMapping("/register-user")
    public String registerUser(Model model) {
        User user =new User();
        model.addAttribute("user", user);
        return "register-user";
    }
    @PostMapping("/user-saved")
    public String addUserToDatabase(Model model, @ModelAttribute User user)
    {
        model.addAttribute("user", user);
        userService.saveUser(user);
        return "user-saved";

    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Delete the JWT cookie
        System.out.println("cookie deleted");
        Cookie cookie = new Cookie("jwt", null); // Same name as your token
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // only if you're using HTTPS
        cookie.setPath("/"); // match the path of the original cookie
        cookie.setMaxAge(0); // delete the cookie
        response.addCookie(cookie);
        System.out.println("cookie deleted");
        // Redirect to login
        return "redirect:/login";
    }

}
