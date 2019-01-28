package com.kang.core.blog.blog.controlller;

import com.kang.core.blog.blog.domain.Blog;
import com.kang.core.blog.blog.domain.Comment;
import com.kang.core.blog.blog.domain.User;
import com.kang.core.blog.blog.service.BlogService;
import com.kang.core.blog.blog.service.CommentService;
import com.kang.core.blog.blog.util.ConstraintViolationExceptionHandler;
import com.kang.core.blog.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public String listCommens(@RequestParam(value = "blogId",required = true) Long blogId , Model model){
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getComments();
        // 判断操作用户是否是评论的所有者
        String commentOwner = "";
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                commentOwner = principal.getUsername();
            }
        }
        model.addAttribute("commentOwner", commentOwner);
        model.addAttribute("comments", comments);
        return "/userspace/blog :: #mainContainerRepleace";
    }

    /**
     * 发表评论
     * @param blogId
     * @param commentContent
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> createComment(Long blogId, String commentContent) {

        try {
            blogService.createComment(blogId, commentContent);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }
}
