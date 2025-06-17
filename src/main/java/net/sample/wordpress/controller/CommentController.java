package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sample.wordpress.entity.Comments;
import net.sample.wordpress.service.CommentService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    UserService userService;
    @GetMapping("/comment")
    public String getCommentsByUser( @RequestParam(required = false) Long blogId,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        String token = extractJwtFromCookies(request);
        if (token == null) return "redirect:/login";

        Long userId = jwtService.extractUserId(token);
        String username = userService.findById(userId).getUsername();

        List<Comments> comments;

        // If blogId is passed, get comments for that blog only
        if (blogId != null) {
            comments = commentService.getCommentsByBlog(blogId);
        } else {
            comments = commentService.getComments(username);
        }

        redirectAttributes.addFlashAttribute("comments", comments);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/user/home-page/show-all-blogs");
    }

    // POST a new comment
    @PostMapping("/post")
    public String postComment(HttpServletRequest request,
                              @RequestParam String commentText,
                              @RequestParam Long blogId,
                              RedirectAttributes redirectAttributes) {
        String token=extractJwtFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        Long userId= jwtService.extractUserId(token);
        String username= userService.findById(userId).getUsername();


        commentService.postComment(username, commentText, blogId);

        redirectAttributes.addFlashAttribute("message", "Comment posted successfully!");

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/user/home-page/show-all-blogs");

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
