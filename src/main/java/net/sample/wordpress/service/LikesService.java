package net.sample.wordpress.service;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Likes;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.LikesRepository;
import net.sample.wordpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;



    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;



    public String likeBlog(Long userId ,Long blogId) {


        boolean alreadyLiked = likesRepository.existsByUser_UserIdAndBlog_BlogId(userId, blogId);
        if (alreadyLiked) {
            Likes existingLike=likesRepository.findByUser_UserIdAndBlog_BlogId(userId,blogId);
            likesRepository.delete(existingLike);
            return  "User with ID " + userId + " unliked blog " + blogId + "!";
        }



        User user = userService.findById(userId);
        Blog blog = blogService.findById(blogId);
        Likes newLikes = new Likes();
        newLikes.setUser(user);
        newLikes.setBlog(blog);
        newLikes.setLikes(1);
        likesRepository.save(newLikes);

        return "user with ID"+userId+"liked the blog"+blogId+"!";
    }

    public long getLikeCountForBlog(Long blogId) {
        return likesRepository.countByBlog_BlogId(blogId);
    }

    public String getUsernameWhoLiked(Likes likes) {
        // Find like by ID
        Likes like = likesRepository.findById(likes.getLikesId())
                .orElseThrow(() -> new RuntimeException("Like not found"));

        // Fetch associated user
        User user = like.getUser();
        return user.getUsername();
    }
}
