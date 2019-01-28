package com.kang.core.blog.blog.service;

import com.kang.core.blog.blog.domain.Authority;
import com.kang.core.blog.blog.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    /* (non-Javadoc)
     * @see com.waylau.spring.boot.blog.service.AuthorityService#getAuthorityById(java.lang.Long)
     */
    @Override
    public Authority getAuthorityById(Long id) {
        return authorityRepository.getOne(id);
    }
}
