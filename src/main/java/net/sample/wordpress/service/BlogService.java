package net.sample.wordpress.service;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BlogService {

   // @Autowired
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
            System.out.println("Saved blog: " + blog);
        } else {
            throw new IllegalArgumentException("Blog must be associated with a user");
        }
    }

    public void deleteBlogById(long blogId) {
        if (blogRepository.existsById(blogId)) {
            blogRepository.deleteById(blogId);
            System.out.println("Blog deleted: " + blogId);
        } else {
            throw new NoSuchElementException("Blog with ID " + blogId + " not found");
        }
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public List<Long> getAllUserIds() {
        return blogRepository.findAllUserIds();
    }








}
