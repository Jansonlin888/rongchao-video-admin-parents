package com.rongchao.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongchao.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rongchao.bean.AdminUser;
import com.rongchao.pojo.Users;
import com.rongchao.service.UsersService;
import com.rongchao.utils.rongchaoJSONResult;
import com.rongchao.utils.PagedResult;

@Controller
@RequestMapping("users")
public class UsersController {
	static Logger logger = LoggerFactory.getLogger(UsersController.class);

	@Autowired
	private UsersService usersService;
	
	@GetMapping("/showList")
	public String showList() {
		return "users/usersList";
	}
	
	@PostMapping("/list")
	@ResponseBody
	public PagedResult list(Users user , Integer page) {
		
		PagedResult result = usersService.queryUsers(user, page == null ? 1 : page, 10);
		return result;
	}
	

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@PostMapping("login")
	@ResponseBody
	public rongchaoJSONResult userLogin(String username, String password,
			HttpServletRequest request, HttpServletResponse response) {

		// TODO 模拟登陆
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return rongchaoJSONResult.errorMap("用户名和密码不能为空");
		}
		try {
			password = MD5Utils.getMD5Str(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("用户密码是:"+password);
		Users users = new Users();
		users.setUsername(username);
		users.setPassword(password);
		if (usersService.userLogin(users) != null) {
			//username.equals("lee") && password.equals("lee")
			String token = UUID.randomUUID().toString();
			AdminUser user = new AdminUser(username, password, token);
			request.getSession().setAttribute("sessionUser", user);
			return rongchaoJSONResult.ok();
		}

		return rongchaoJSONResult.errorMsg("登陆失败，请重试...");
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute("sessionUser");
		return "login";
	}
	
}
