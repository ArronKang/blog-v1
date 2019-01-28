package com.kang.core.blog.blog.repository;

import com.kang.core.blog.blog.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository  extends JpaRepository<Authority, Long> {
}
