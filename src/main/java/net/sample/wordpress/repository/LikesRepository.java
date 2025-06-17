package net.sample.wordpress.repository;

import net.sample.wordpress.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {


    Optional<Likes> findById(Long likesId);

    boolean existsByUser_UserIdAndBlog_BlogId(Long userId, Long blogId);

    Likes findByUser_UserIdAndBlog_BlogId(Long userId, Long blogId);

    long countByBlog_BlogId(Long blogId);
}
