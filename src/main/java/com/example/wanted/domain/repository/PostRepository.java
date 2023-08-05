package com.example.wanted.domain.repository;

import com.example.wanted.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findPostByOrderByIdDesc(PageRequest pageRequest);
}
