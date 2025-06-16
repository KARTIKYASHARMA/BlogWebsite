package net.sample.wordpress.repository;

import net.sample.wordpress.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("SELECT DISTINCT b.user.userId FROM Blog b")
    List<Long> findAllUserIds();

}
