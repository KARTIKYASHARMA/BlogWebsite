package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Likes;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.LikesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
public class LikesController {

    private static final Logger logger = LoggerFactory.getLogger(LikesController.class);
    @Autowired
    private LikesService likesService;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private BlogService blogService;


    @PostMapping("/like")
    public String likeOrUnlikeBlog(@RequestParam Long blogId,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes)
    {
        logger.debug("POST /like request received for blogId={}", blogId);
        String token = extractJwtFromCookies(request);
        if (token == null) {
            logger.warn("JWT not found in cookies. Redirecting to login.");
            return "redirect:/login";
        }
        Long userId=jwtService.extractUserId(token);
        logger.debug("User ID extracted from token: {}", userId);
        Blog blog =blogService.findById(blogId);
        if (blog == null) {
            logger.warn("Blog not found with ID: {}", blogId);
            redirectAttributes.addFlashAttribute("error", "Blog not found.");
            return "redirect:/user/home-page/show-all-blogs";
        }

        String message=likesService.likeBlog(userId,blogId);
        logger.info("Like action completed: {}", message);
        long likeCount=likesService.getLikeCountForBlog(blogId);
        blog.setLikeCount((int) likeCount);
        logger.debug("Updated like count for blog {}: {}", blogId, likeCount);

        redirectAttributes.addFlashAttribute("message",message);
        redirectAttributes.addFlashAttribute("likeCount",likeCount);

        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            referer = "/user/home-page/show-all-blogs"; // fallback
        }
        logger.debug("Redirecting back to: {}", referer);
        return "redirect:" + referer;
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
