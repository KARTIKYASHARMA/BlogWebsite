package net.sample.wordpress.controller;

import jakarta.servlet.http.Cookie;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.*;
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
    @Autowired
    private SubscriptionService subscriptionService;






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

//    @GetMapping("/show-all-blogs")
//    public String showAllBlogs(Model model,HttpServletRequest request) {
//        //String jwtToken = extractJwtFromCookies(request);
//        List<Blog> blogList=blogService.getAllBlogs();
//        logger.debug("Fetched {} total blogs", blogList.size());
//
//        List<Long> userIds = blogList.stream()
//                .map(blog -> blog.getUser() != null ? blog.getUser().getUserId() : null)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.toList());
//
//        //Set<Long> uniqueValues = new HashSet<>();
//        Map<Long, Long> blogIdToLikeCount = new HashMap<>();
//        for (Blog blog : blogList) {
//            blogIdToLikeCount.put(blog.getBlogId(), likesService.getLikeCountForBlog(blog.getBlogId()));
//        }
//        logger.debug("Blog like counts: {}", blogIdToLikeCount);
//
//
//
//        List<User> users =userService.findAllById(userIds);
//        Map<Long, String> userIdToUsername = users.stream()
//                .collect(Collectors.toMap(User::getUserId, User::getUsername));
//
//        logger.debug("Fetched {} users for blog attribution", users.size());
//
//        model.addAttribute("blogLikeCounts", blogIdToLikeCount);
//        model.addAttribute("blogList", blogList);
//        model.addAttribute("usernames",userIdToUsername);
//
//        String token = extractJwtFromCookies(request);
//
//        if (token != null) {
//            Long userId = jwtService.extractUserId(token);
//            Set<Long> likedBlogs = likesService.getBlogIdsLikedByUser(userId);
//            model.addAttribute("currentUserId", userId);
//            model.addAttribute("likedBlogs", likedBlogs);
//            User user = userService.findById(userId);
//            if (user != null) {
//                model.addAttribute("currentUsername", user.getUsername());
//            }
//            logger.debug("User ID {} has liked {} blogs", userId, likedBlogs.size());
//        } else {
//            model.addAttribute("currentUserId", null);
//            model.addAttribute("likedBlogs", Set.of());
//            logger.debug("No JWT found, guest user viewing blogs.");
//        }
//
//
//        return "show-all-blogs";
//
//
//    }

    @GetMapping("/show-all-blogs")
    public String showAllBlogs(Model model, HttpServletRequest request) {
        List<Blog> blogList = blogService.getAllBlogs();
        logger.debug("Fetched {} total blogs", blogList.size());

        List<Long> userIds = blogList.stream()
                .map(blog -> blog.getUser() != null ? blog.getUser().getUserId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Long> blogIdToLikeCount = new HashMap<>();
        for (Blog blog : blogList) {
            blogIdToLikeCount.put(blog.getBlogId(), likesService.getLikeCountForBlog(blog.getBlogId()));
        }

        List<User> users = userService.findAllById(userIds);
        Map<Long, String> userIdToUsername = users.stream()
                .collect(Collectors.toMap(User::getUserId, User::getUsername));

        model.addAttribute("blogLikeCounts", blogIdToLikeCount);
        model.addAttribute("blogList", blogList);
        model.addAttribute("usernames", userIdToUsername);

        String token = extractJwtFromCookies(request);

        if (token != null) {
            Long userId = jwtService.extractUserId(token);
            Set<Long> likedBlogs = likesService.getBlogIdsLikedByUser(userId);
            model.addAttribute("currentUserId", userId);
            model.addAttribute("likedBlogs", likedBlogs);

            User user = userService.findById(userId);
            if (user != null) {
                model.addAttribute("currentUsername", user.getUsername());

                // ✅ ADD subscription info
                boolean hasActiveSub = subscriptionService.hasActiveSubscription(user);
                long daysLeft = subscriptionService.daysRemaining(user);
                model.addAttribute("hasActiveSub", hasActiveSub);
                model.addAttribute("daysLeft", daysLeft);
            }
        } else {
            model.addAttribute("currentUserId", null);
            model.addAttribute("likedBlogs", Set.of());
            model.addAttribute("hasActiveSub", false); // fallback default
            model.addAttribute("daysLeft", 0L);         // fallback default
            logger.debug("No JWT found, guest user viewing blogs.");
        }

        return "show-all-blogs";
    }

    @GetMapping("/show-blog")
    public String showUserBlog(Model model, HttpServletRequest request) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            logger.warn("JWT token missing in show-blog request. Redirecting to login.");
            return "redirect:/login";
        }

        Long userId = jwtService.extractUserId(jwtToken);
        User user = userService.findById(userId);
        if (user == null) {
            logger.error("User not found with ID: {}", userId);
            return "user-not-found";
        }

        List<Blog> blogList = user.getBlogs();
        logger.info("User {} has {} blogs", user.getUsername(), blogList.size());

        // Prepare blogLikeCounts
        Map<Long, Long> blogIdToLikeCount = new HashMap<>();
        for (Blog blog : blogList) {
            blogIdToLikeCount.put(blog.getBlogId(), likesService.getLikeCountForBlog(blog.getBlogId()));
        }

        // Usernames map (only one entry)
        Map<Long, String> usernames = Map.of(userId, user.getUsername());

        // Blogs liked by user
        Set<Long> likedBlogs = likesService.getBlogIdsLikedByUser(userId);

        model.addAttribute("blogList", blogList);
        model.addAttribute("blogLikeCounts", blogIdToLikeCount);
        model.addAttribute("usernames", usernames);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("likedBlogs", likedBlogs);

        return "show-blog"; // Reuse the same template structure as 'show-all-blogs'
    }

    @GetMapping("/show-full-blog/{id}")
    public String viewBlog(@PathVariable Long id, Model model, HttpServletRequest request) {
        Blog blog = blogService.findById(id);
        if (blog == null) {
            return "user-not-found"; // or your custom "blog not found" page
        }
        blog.setViews(blog.getViews() + 1);
        blogService.saveBlog(blog);

        // Add the blog itself
        model.addAttribute("blog", blog);

        // Extract JWT
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken != null) {
            Long userId = jwtService.extractUserId(jwtToken);
            model.addAttribute("currentUserId", userId);

            // Get liked blogs by user
            Set<Long> likedBlogs = likesService.getBlogIdsLikedByUser(userId);
            model.addAttribute("likedBlogs", likedBlogs);
        } else {
            model.addAttribute("currentUserId", null);
            model.addAttribute("likedBlogs", Set.of());
        }

        // Add blog like count
        long likeCount = likesService.getLikeCountForBlog(blog.getBlogId());
        model.addAttribute("blogLikeCount", likeCount);

        return "show-full-blog"; // View template
    }

    @GetMapping("/user-profile")
    public String userProfile(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id); // fetch user by ID
        if (user == null) {
            return "error-page"; // handle user not found
        }

        model.addAttribute("user", user);
        model.addAttribute("hasActiveSub", user.hasActiveSubscription());
        model.addAttribute("daysRemaining", user.daysRemainingInSubscription());

        return "user-profile";
    }

    @GetMapping("/delete-blog/{id}")
    public String deleteBlog(@PathVariable Long id, HttpServletRequest request) {
        String token = extractJwtFromCookies(request);
        if (token == null) {
            logger.warn("Attempt to delete blog without JWT. Redirecting to login.");
            return "redirect:/login";
        }

        Long currentUserId = jwtService.extractUserId(token);
        Blog blog = blogService.findById(id);

        if (blog == null) {
            logger.warn("Attempted to delete non-existent blog with ID: {}", id);
            return "user-not-found"; // or a proper error page
        }

        // Authorization: ensure user owns the blog
        if (blog.getUser() == null || blog.getUser().getUserId() == null || !blog.getUser().getUserId().equals(currentUserId)) {
            logger.warn("User ID {} attempted to delete blog ID {} not owned by them.", currentUserId, id);
            return "unauthorized"; // you can create a Thymeleaf template named `unauthorized.html`
        }

        blogService.deleteBlogById(id);
        logger.info("User ID {} deleted blog ID {}", currentUserId, id);

        return "redirect:/user/home-page/show-blog"; // Redirect to user's blog list
    }
    @GetMapping("/edit-blog/{id}")
    public String editBlogForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) return "redirect:/login";

        Long userId = jwtService.extractUserId(jwtToken);
        Blog blog = blogService.findById(id);

        if (blog == null || !blog.getUser().getUserId().equals(userId)) {
            return "unauthorized"; // Or error page
        }

        model.addAttribute("blog", blog);
        return "fragments/edit-blog-modal :: editBlogForm"; // Only return modal content
    }

    @PostMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id,
                             @ModelAttribute Blog updatedBlog,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             HttpServletRequest request) {
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) return "redirect:/login";

        Long userId = jwtService.extractUserId(jwtToken);
        Blog existingBlog = blogService.findById(id);

        if (existingBlog == null || !existingBlog.getUser().getUserId().equals(userId)) {
            return "unauthorized";
        }

        existingBlog.setTitle(updatedBlog.getTitle());
        existingBlog.setContent(updatedBlog.getContent());
       // existingBlog.setImagePath(updatedBlog.getImagePath());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(uploadsDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = java.util.UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                String filePath = uploadsDir + fileName;
                imageFile.transferTo(new java.io.File(filePath));
                existingBlog.setImagePath("/uploads/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        blogService.saveBlog(existingBlog);
        return "redirect:/user/home-page/show-blog";
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