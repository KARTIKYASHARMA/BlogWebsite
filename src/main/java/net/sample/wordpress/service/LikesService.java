package net.sample.wordpress.service;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Likes;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.LikesRepository;
import net.sample.wordpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class LikesService {

    private static final Logger logger = LoggerFactory.getLogger(LikesService.class);

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;



    public String likeBlog(Long userId ,Long blogId) {
        logger.debug("User {} attempting to like/unlike blog {}", userId, blogId);
        Likes existingLike = likesRepository.findByUser_UserIdAndBlog_BlogId(userId, blogId);boolean alreadyLiked = likesRepository.existsByUser_UserIdAndBlog_BlogId(userId, blogId);
        if (existingLike != null) {
            likesRepository.delete(existingLike);
            logger.info("User {} unliked blog {}", userId, blogId);
            return  "User with ID " + userId + " unliked blog " + blogId + "!";
        }



        User user = userService.findById(userId);
        Blog blog = blogService.findById(blogId);
        Likes newLikes = new Likes();
        newLikes.setUser(user);
        newLikes.setBlog(blog);
        likesRepository.save(newLikes);
        logger.info("User {} liked blog {}", userId, blogId);
        return "user with ID"+userId+"liked the blog"+blogId+"!";
    }

    public long getLikeCountForBlog(Long blogId) {
        long count = likesRepository.countByBlog_BlogId(blogId);
        logger.debug("Blog {} has {} likes", blogId, count);
        return count;
    }
    public Set<Long> getBlogIdsLikedByUser(Long userId) {
        logger.debug("Fetching liked blog IDs for user {}", userId);
        List<Likes> likes = likesRepository.findByUser_UserId(userId);
         Set<Long> blogIds=likes.stream()
                .map(like -> like.getBlog().getBlogId())
                .collect(Collectors.toSet());
        logger.info("User {} has liked blogs: {}", userId, blogIds);
        return blogIds;
    }

    public String getUsernameWhoLiked(Likes likes) {
        // Find like by ID
        logger.debug("Fetching username for like ID {}", likes.getLikesId());
        Likes like = likesRepository.findById(likes.getLikesId())
                .orElseThrow(() -> {
                    logger.warn("Like with ID {} not found", likes.getLikesId());
                    return new RuntimeException("Like not found");});

        // Fetch associated user
        User user = like.getUser();
        String username= user.getUsername();
        logger.info("Like ID {} is associated with username: {}", likes.getLikesId(), username);
        return  username;
    }
}
