package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user/home-page")
public class WordPressBlogController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;
    @Autowired
    JwtService jwtService;


    //private HttpServletRequest request;



    @GetMapping("/add-blog/")
    public String addBlog(Model model,HttpServletRequest request) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            return "redirect:/login";
        }
        Long userId=jwtService.extractUserId(jwtToken);
        User user= userService.findById(userId);

        if(user==null)
        {
            return "user-not-found";
        }
        System.out.println("User: "+user.getUserId());
        Blog blog = new Blog();
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("blog", blog);
        return "add-blog";
    }

    @PostMapping("/blog/save/{userId}")
    public String saveBlog(@ModelAttribute Blog blog, @PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return "user-not-found";
        }

        blog.setUser(user); // assuming WordPressBlog has a `User` reference
        blogService.saveBlog(blog);

        return "redirect:/user/home-page/show-all-blogs"; // or any success page
    }


    // get all blogs

    @GetMapping("/show-all-blogs")
    public String showAllBlogs(Model model,HttpServletRequest request) {
        //String jwtToken = extractJwtFromCookies(request);
        List<Blog> blogList=blogService.getAllBlogs();


        List<Long> userIds = new ArrayList<>();
        Set<Long> uniqueValues = new HashSet<>();
        for (Blog blog : blogList) {
            Long userId = blog.getUser().getUserId();
            if (uniqueValues.add(userId)) {
                userIds.add(userId);
            }
        }

        List<User> users =userService.findAllById(userIds);

        Map<Long, String> userIdToUsername = users.stream()
                .collect(Collectors.toMap(User::getUserId, User::getUsername));



        model.addAttribute("blogList", blogList);
        model.addAttribute("usernames",userIdToUsername);
        return "show-all-blogs";


    }

    @GetMapping("/show-blog/{userId}")
    public String showUserBlog(@PathVariable long userId, Model model)
    {
        User user=userService.findById(userId);
        if (user == null) {
            return "user-not-found";  // You can create a page to show user not found
        }

        List<Blog> blogList =user.getBlogs();
        System.out.println("User: " + user.getUserId()+ ", Blogs: " + user.getBlogs().size());

        model.addAttribute("blogList", blogList);

        return "show-blog";
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
