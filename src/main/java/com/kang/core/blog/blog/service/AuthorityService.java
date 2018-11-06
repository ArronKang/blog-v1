package com.kang.core.blog.blog.service;

import com.kang.core.blog.blog.domain.Authority;

public interface AuthorityService {
    /**
     * 根据ID查询 Authority
     * @param id
     * @return
     */
    Authority getAuthorityById(Long id);
}
