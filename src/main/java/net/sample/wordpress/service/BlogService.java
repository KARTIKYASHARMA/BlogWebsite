package net.sample.wordpress.service;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.BlogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BlogService {

   // @Autowired
   private static final Logger logger = LoggerFactory.getLogger(BlogService.class);
    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository)
    {
        this.blogRepository=blogRepository;
    }



    public void saveBlog(Blog blog)
    {
        if (blog.getUser() != null) {
            blog.getUser().addBlog(blog);
            blogRepository.save(blog);
            logger.info("Saved blog with ID: {} by user ID: {}", blog.getBlogId(), blog.getUser().getUserId());
        } else {
            logger.warn("Attempted to save blog without a user");
            throw new IllegalArgumentException("Blog must be associated with a user");
        }
    }

    public void deleteBlogById(long blogId) {
        if (blogRepository.existsById(blogId)) {
            blogRepository.deleteById(blogId);
            logger.info("Deleted blog with ID: {}", blogId);
        } else {
            logger.warn("Attempted to delete non-existent blog with ID: {}", blogId);
            throw new NoSuchElementException("Blog with ID " + blogId + " not found");
        }
    }

    public List<Blog> getAllBlogs() {
        logger.debug("Fetching all blogs");
        return blogRepository.findAll();
    }

    public List<Long> getAllUserIds() {

        logger.debug("Fetching all user IDs from blogs");
        return blogRepository.findAllUserIds();
    }


    public Blog findById(Long blogId) {

        logger.debug("Finding blog by ID: {}", blogId);
        return blogRepository.findById(blogId).orElse(null);
    }


}
