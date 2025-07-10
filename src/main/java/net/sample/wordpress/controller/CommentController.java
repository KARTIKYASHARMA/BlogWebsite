package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sample.wordpress.entity.Comments;
import net.sample.wordpress.service.CommentService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
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
        logger.debug("GET /comment requested. blogId={}", blogId);
        String token = extractJwtFromCookies(request);
        if (token == null) {
            logger.warn("No JWT found in cookies. Redirecting to login.");
            return "redirect:/login";
        }

        Long userId = jwtService.extractUserId(token);
        String username = userService.findById(userId).getUsername();
        logger.debug("Extracted username: {} from token", username);

        List<Comments> comments;

        // If blogId is passed, get comments for that blog only
        if (blogId != null) {
            comments = commentService.getCommentsByBlog(blogId);
            logger.info("Retrieved {} comments for blogId={}", comments.size(), blogId);
        } else {
            comments = commentService.getComments(username);
            logger.info("Retrieved {} comments for user={}", comments.size(), username);
        }

        redirectAttributes.addFlashAttribute("comments", comments);
        String referer = request.getHeader("Referer");
        logger.debug("Redirecting back to: {}", referer);
        return "redirect:" + (referer != null ? referer : "/user/home-page/show-all-blogs");
    }

    // POST a new comment
    @PostMapping("/post")
    public String postComment(HttpServletRequest request,
                              @RequestParam String commentText,
                              @RequestParam Long blogId,
                              RedirectAttributes redirectAttributes) {
        logger.debug("POST /post requested. blogId={}, commentText={}", blogId, commentText);
        String token=extractJwtFromCookies(request);
        if (token == null) {
            logger.warn("No JWT found in cookies during comment post. Redirecting to login.");
            return "redirect:/login";
        }
        Long userId= jwtService.extractUserId(token);
        String username= userService.findById(userId).getUsername();
        logger.info("User {} is posting a comment on blog {}", username, blogId);
        commentService.postComment(username, commentText, blogId);
        logger.info("Comment successfully posted by user {}", username);

        redirectAttributes.addFlashAttribute("message", "Comment posted successfully!");

        String referer = request.getHeader("Referer");
        logger.debug("Redirecting back to: {}", referer);
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
