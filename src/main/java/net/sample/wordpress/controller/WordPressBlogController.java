package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.BlogService;
import net.sample.wordpress.service.JwtService;
import net.sample.wordpress.service.LikesService;
import net.sample.wordpress.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user/home-page")
public class WordPressBlogController {

    private static final Logger logger = LoggerFactory.getLogger(WordPressBlogController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private LikesService likesService;





    @GetMapping("/add-blog/")
    public String addBlog(Model model,HttpServletRequest request) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            logger.warn("JWT token missing in add-blog GET request. Redirecting to login.");
            return "redirect:/login";
        }
        Long userId=jwtService.extractUserId(jwtToken);
        logger.debug("Rendering add-blog page for user ID: {}", userId);

        Blog blog = new Blog();
        model.addAttribute("userId", userId);
        model.addAttribute("blog", blog);
        return "add-blog";
    }

    @PostMapping("/blog/save/{userId}")
    public String saveBlog(@ModelAttribute Blog blog, @PathVariable Long userId,@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        logger.info("Attempting to save blog for user ID: {}", userId);
        User user = userService.findById(userId);
        if (user == null) {
            logger.error("User not found with ID: {}", userId);
            return "user-not-found";
        }

        blog.setUser(user); // assuming WordPressBlog has a `User` reference
        // Set creation date if not already set
        if (blog.getCreatedDate() == null) {
            blog.setCreatedDate(new java.util.Date());
        }
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(uploadsDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = java.util.UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                String filePath = uploadsDir + fileName;
                imageFile.transferTo(new java.io.File(filePath));
                blog.setImagePath("/uploads/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally add error handling
            }
        }
        blogService.saveBlog(blog);
        logger.info("Blog saved successfully for user: {}", user.getUsername());


        return "redirect:/user/home-page/show-all-blogs"; // or any success page
    }
    // Serve uploaded images statically
    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public org.springframework.core.io.Resource serveImage(@PathVariable String filename) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get(System.getProperty("user.dir") + "/uploads/", filename);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    // get all blogs

    @GetMapping("/show-all-blogs")
    public String showAllBlogs(Model model,HttpServletRequest request) {
        //String jwtToken = extractJwtFromCookies(request);
        List<Blog> blogList=blogService.getAllBlogs();
        logger.debug("Fetched {} total blogs", blogList.size());

        List<Long> userIds = blogList.stream()
                .map(blog -> blog.getUser() != null ? blog.getUser().getUserId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        //Set<Long> uniqueValues = new HashSet<>();
        Map<Long, Long> blogIdToLikeCount = new HashMap<>();
        for (Blog blog : blogList) {
            blogIdToLikeCount.put(blog.getBlogId(), likesService.getLikeCountForBlog(blog.getBlogId()));
        }
        logger.debug("Blog like counts: {}", blogIdToLikeCount);



        List<User> users =userService.findAllById(userIds);
        Map<Long, String> userIdToUsername = users.stream()
                .collect(Collectors.toMap(User::getUserId, User::getUsername));

        logger.debug("Fetched {} users for blog attribution", users.size());

        model.addAttribute("blogLikeCounts", blogIdToLikeCount);
        model.addAttribute("blogList", blogList);
        model.addAttribute("usernames",userIdToUsername);
        String token = extractJwtFromCookies(request);

        if (token != null) {
            Long userId = jwtService.extractUserId(token);
            Set<Long> likedBlogs = likesService.getBlogIdsLikedByUser(userId);
            model.addAttribute("currentUserId", userId);
            model.addAttribute("likedBlogs", likedBlogs);
            logger.debug("User ID {} has liked {} blogs", userId, likedBlogs.size());
        } else {
            model.addAttribute("currentUserId", null);
            model.addAttribute("likedBlogs", Set.of());
            logger.debug("No JWT found, guest user viewing blogs.");
        }


        return "show-all-blogs";


    }

    @GetMapping("/show-blog")
    public String showUserBlog( Model model,HttpServletRequest request)
    {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            logger.warn("JWT token missing in show-blog request. Redirecting to login.");
            return "redirect:/login";}
        Long userId=jwtService.extractUserId(jwtToken);
        User user=userService.findById(userId);
        if (user == null) {
            logger.error("User not found with ID: {}", userId);
            return "user-not-found";  // You can create a page to show user not found
        }

        List<Blog> blogList =user.getBlogs();

        logger.info("User {} has {} blogs", user.getUsername(), blogList.size());
        model.addAttribute("blogList", blogList);

        return "show-blog";
    }
    @GetMapping("/show-full-blog/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.findById(id); // or however you retrieve
        model.addAttribute("blog", blog);
        return "show-full-blog"; // your detail view template
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