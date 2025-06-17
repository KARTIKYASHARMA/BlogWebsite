package net.sample.wordpress.service;


import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.Comments;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    UserService userService;
    @Autowired
    BlogService blogService;
    @Autowired
    CommentsRepository commentsRepository;

    public List<Comments> getComments(String username) {


        User user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");
        List<Comments> commentsList = commentsRepository.findAllByUser_UserId(user.getUserId());


        return commentsList;
    }
    public List<Comments> getCommentsByBlog(Long blogId) {
        return commentsRepository.findAllByBlog_BlogId(blogId);
    }

    public String postComment( String username,String commentText,Long blogId) {
        User user= userService.findByUsername(username);
        Blog blog= blogService.findById(blogId);


        if (user == null) throw new RuntimeException("User not found");

        if (blog == null) throw new RuntimeException("Blog not found");
        Comments comments= new Comments();
        comments.setUser(user);
        comments.setBlog(blog);
        comments.setComment(commentText);
        commentsRepository.save(comments);
        return "comment posted";


    }
}
