package com.kang.core.blog.blog.service;

import com.kang.core.blog.blog.domain.Blog;
import com.kang.core.blog.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    /**
     * 保存Blog
     * @param EsBlog
     * @return
     */
    Blog saveBlog(Blog blog);

    /**
     * 删除Blog
     * @param id
     * @return
     */
    void removeBlog(Long id);

    /**
     * 根据id获取Blog
     * @param id
     * @return
     */
    Blog getBlogById(Long id);

    /**
     * 根据用户进行博客名称分页模糊查询（最新）
     * @param user
     * @return
     */
    Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable);

    /**
     * 根据用户进行博客名称分页模糊查询（最热）
     * @param user
     * @return
     */
    Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable);

    /**
     * 阅读量递增
     * @param id
     */
    void readingIncrease(Long id);

    Blog createComment(Long blogId, String commentContent);


}
