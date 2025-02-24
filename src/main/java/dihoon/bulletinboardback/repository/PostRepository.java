package dihoon.bulletinboardback.repository;

import dihoon.bulletinboardback.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByUser_UserId(Long userId, Pageable pageable);
}
