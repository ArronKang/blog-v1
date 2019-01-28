package com.kang.core.blog.blog.service;

import com.kang.core.blog.blog.domain.Comment;

public interface CommentService {

    /**
     * 根据id获取 Comment
     * @param id
     * @return
     */
    Comment getCommentById(Long id);
    /**
     * 删除评论
     * @param id
     * @return
     */
    void removeComment(Long id);
}
