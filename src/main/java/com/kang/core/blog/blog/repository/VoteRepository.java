package com.kang.core.blog.blog.repository;

import com.kang.core.blog.blog.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
