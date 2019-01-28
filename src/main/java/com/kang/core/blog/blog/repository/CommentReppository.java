package com.kang.core.blog.blog.repository;

import com.kang.core.blog.blog.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReppository extends JpaRepository<Comment,Long> {
}
