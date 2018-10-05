package com.kang.core.blog.blog.repository;


import com.kang.core.blog.blog.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * User Repository 接口.
 *
 */
public interface UserRepository extends CrudRepository<User,Long> {
}
