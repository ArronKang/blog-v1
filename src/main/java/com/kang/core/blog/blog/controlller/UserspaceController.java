package com.kang.core.blog.blog.controlller;

import com.kang.core.blog.blog.domain.Blog;
import com.kang.core.blog.blog.domain.User;
import com.kang.core.blog.blog.domain.Vote;
import com.kang.core.blog.blog.service.BlogService;
import com.kang.core.blog.blog.service.UserService;
import com.kang.core.blog.blog.util.ConstraintViolationExceptionHandler;
import com.kang.core.blog.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 用户主页控制器.
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {
	@Autowired
	private UserService userService;

	@Autowired
	private BlogService blogService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Value("${file.server.url}")
	private String fileServerUrl;



	/**
	 * 用户的主页
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}")
	public String userSpace(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return "redirect:/u/" + username + "/blogs";
	}
	/**
	 * 获取用户的博客列表
	 * @param username
	 * @param order
	 * @param catalogId
	 * @param keyword
	 * @param async
	 * @param pageIndex
	 * @param pageSize
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs")
	public String listBlogsByOrder(@PathVariable("username") String username,
								   @RequestParam(value="order",required=false,defaultValue="new") String order,
								   @RequestParam(value="catalog",required=false ) Long catalogId,
								   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
								   @RequestParam(value="async",required=false) boolean async,
								   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
								   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
								   Model model) {

		User  user = (User)userDetailsService.loadUserByUsername(username);

		Page<Blog> page = null;

		if (catalogId != null && catalogId > 0) { // 分类查询
			// TODO
		} else if (order.equals("hot")) { // 最热查询
			Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
		} else if (order.equals("new")) { // 最新查询
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = blogService.listBlogsByTitleVote(user, keyword, pageable);
		}


		List<Blog> list = page.getContent();	// 当前所在页面数据列表

		model.addAttribute("user", user);
		model.addAttribute("order", order);
		model.addAttribute("catalogId", catalogId);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list);
		return (async==true?"/userspace/u :: #mainContainerRepleace":"/userspace/u");
	}


	/**
	 * 获取编辑博客的界面
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs/edit/{id}")
	public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
		model.addAttribute("blog", blogService.getBlogById(id));
		model.addAttribute("fileServerUrl", fileServerUrl);// 文件服务器的地址返回给客户端
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}


	/**
	 * 获取博客展示界面
	 * @param username
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs/{id}")
	public String getBlogById(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {
		User principal = null;
		Blog blog = blogService.getBlogById(id);

		// 每次读取，简单的可以认为阅读量增加1次
		blogService.readingIncrease(id);

		// 判断操作用户是否是博客的所有者
		boolean isBlogOwner = false;
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				&&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal !=null && username.equals(principal.getUsername())) {
				isBlogOwner = true;
			}
		}
		// 判断操作用户的点赞情况
		List<Vote> votes = blog.getVotes();
		Vote currentVote = null; // 当前用户的点赞情况

		if (principal !=null) {
			for (Vote vote : votes) {
				vote.getUser().getUsername().equals(principal.getUsername());
				currentVote = vote;
				break;
			}
		}
		model.addAttribute("currentVote",currentVote);
		model.addAttribute("isBlogOwner", isBlogOwner);
		model.addAttribute("blogModel",blog);

		return "/userspace/blog";
	}



	/**
	 * 获取新增博客的界面
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs/edit")
	public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
		model.addAttribute("blog", new Blog(null, null, null));
		model.addAttribute("fileServerUrl", fileServerUrl);// 文件服务器的地址返回给客户端
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}


	/**
	 * 保存博客
	 * @param username
	 * @param blog
	 * @return
	 */
	@PostMapping("/{username}/blogs/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {

		try {

			// 判断是修改还是新增
			if (blog.getId()!=null) {
				Blog orignalBlog = blogService.getBlogById(blog.getId());
				orignalBlog.setTitle(blog.getTitle());
				orignalBlog.setContent(blog.getContent());
				orignalBlog.setSummary(blog.getSummary());
				blogService.saveBlog(orignalBlog);
			} else {
				User user = (User)userDetailsService.loadUserByUsername(username);
				blog.setUser(user);
				blogService.saveBlog(blog);
			}

		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
}