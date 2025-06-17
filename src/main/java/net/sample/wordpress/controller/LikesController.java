package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Likes;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LikesController {

    @Autowired
    private LikesService likesService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/like")
    public String likeOrUnlikeBlog(@RequestParam Long blogId,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes)
    {
        String token = extractJwtFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        Long userId=jwtService.extractUserId(token);

        String message=likesService.likeBlog(userId,blogId);
        long likeCount=likesService.getLikeCountForBlog(blogId);
        redirectAttributes.addFlashAttribute("message",message);
        redirectAttributes.addFlashAttribute("likeCount",likeCount);
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            referer = "/user/home-page/show-all-blogs"; // fallback
        }

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
