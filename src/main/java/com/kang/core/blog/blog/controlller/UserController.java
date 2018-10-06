package com.kang.core.blog.blog.controlller;

import com.kang.core.blog.blog.domain.User;
import com.kang.core.blog.blog.service.UserService;
import com.kang.core.blog.blog.util.ConstraintViolationExceptionHandler;
import com.kang.core.blog.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * User 控制器.
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	/**
	 * 查询所有用户
	 * @param model
	 * @return
	 */
	@GetMapping
	public ModelAndView list(@RequestParam(value="async",required=false) boolean async,
							 @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
							 @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
							 @RequestParam(value="name",required=false,defaultValue="") String name,
							 Model model) {
		Pageable pageable= PageRequest.of(pageIndex,pageSize);
		Page<User> page=userService.listUsersByNameLike(name,pageable);
		List<User> list=page.getContent();
		model.addAttribute("page",page);
		model.addAttribute("userList",list);
		return new ModelAndView(async==true?"users/list :: #mainContainerRepleace":"users/list","userModel",model);
	}

	/**
	 * 保存或者修改用户
	 * @param user
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Response> saveOrUpateUser(User user) {

		try {
			userService.saveOrUpateUser(user);
		}  catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		}
		return ResponseEntity.ok().body(new Response(true, "处理成功", user));
	}


	
	/**
	 * 获取创建表单页面
	 * @param model
	 * @return
	 */
	@GetMapping("/add")
	public ModelAndView createForm(Model model) {
		model.addAttribute("user", new User(null, null, null,null));
		return new ModelAndView("users/add", "userModel", model);
	}

	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	@GetMapping("/delete/{id}")
	public ResponseEntity<Response>  delete(@PathVariable("id") Long id) {
		try {
			userService.removeUser(id);
		} catch (Exception e) {
			return  ResponseEntity.ok().body( new Response(false, e.getMessage()));
		}
		return  ResponseEntity.ok().body( new Response(true, "处理成功"));
	}
	
	/**
	 * 获取修改用户的界面
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping(value = "edit/{id}")
	public ModelAndView modifyForm(@PathVariable("id") Long id, Model model) {
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return new ModelAndView("users/edit", "userModel", model);
	}
}
