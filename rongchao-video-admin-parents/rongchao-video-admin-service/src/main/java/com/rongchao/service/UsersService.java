package com.rongchao.service;

import com.rongchao.pojo.Users;
import com.rongchao.utils.PagedResult;

public interface UsersService {

	public PagedResult queryUsers(Users user, Integer page, Integer pageSize);

}
