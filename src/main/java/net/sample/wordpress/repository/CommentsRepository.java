package net.sample.wordpress.repository;

import net.sample.wordpress.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {


    List<Comments> findAllByUser_UserId(long userId);

    List<Comments> findAllByBlog_BlogId(Long blogId);
}
