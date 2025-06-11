package net.sample.wordpress.controller;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/home-page")
public class WordPressBlogController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/add-blog/{userId}")
    public String addBlog(Model model,@PathVariable long userId) {

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

        return "redirect:/home-page/"; // or any success page
    }


    // get all blogs

    @GetMapping("/show-all-blogs")
    public String showAllBlogs(Model model) {
        List<Blog> blogList=blogService.getAllBlogs();
        model.addAttribute("blogList", blogList);
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


}
