package net.sample.wordpress.repository;

import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Optional<Blog> findById(Long id);
    @Query("SELECT DISTINCT b.user.userId FROM Blog b")
    List<Long> findAllUserIds();

}
