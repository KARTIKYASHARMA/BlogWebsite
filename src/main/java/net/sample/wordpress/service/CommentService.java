package net.sample.wordpress.service;


import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Comments;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.CommentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    UserService userService;
    @Autowired
    BlogService blogService;
    @Autowired
    CommentsRepository commentsRepository;

    public List<Comments> getComments(String username) {

        logger.debug("Fetching comments for user: {}", username);
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new RuntimeException("User not found");
        }
        List<Comments> commentsList = commentsRepository.findAllByUser_UserId(user.getUserId());
        logger.info("Found {} comments for user: {}", commentsList.size(), username);
        return commentsList;
    }
    public List<Comments> getCommentsByBlog(Long blogId) {
        logger.debug("Fetching comments for blog ID: {}", blogId);
        List<Comments> comments = commentsRepository.findAllByBlog_BlogId(blogId);
        logger.info("Found {} comments for blog ID: {}", comments.size(), blogId);
        return commentsRepository.findAllByBlog_BlogId(blogId);
    }

    public String postComment( String username,String commentText,Long blogId) {
        logger.debug("Posting comment by user: {} on blog ID: {}", username, blogId);

        User user= userService.findByUsername(username);
        Blog blog= blogService.findById(blogId);


        if (user == null){
            logger.warn("User not found: {}", username);
            throw new RuntimeException("User not found");}

        if (blog == null) {
            logger.warn("Blog not found with ID: {}", blogId);
            throw new RuntimeException("Blog not found");
        }
        Comments comments= new Comments();
        comments.setUser(user);
        comments.setBlog(blog);
        comments.setComment(commentText);
        commentsRepository.save(comments);
        logger.info("Comment posted by user: {} on blog ID: {}", username, blogId);
        return "comment posted";



    }
}
