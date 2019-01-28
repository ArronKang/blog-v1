package com.kang.core.blog.blog.service;

import com.kang.core.blog.blog.domain.Comment;
import com.kang.core.blog.blog.repository.CommentReppository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentReppository commentReppository;

    public Comment getCommentById(Long id){
        return commentReppository.getOne(id);
    }
    public void removeComment(Long id) {
        commentReppository.deleteById(id);
    }



}
