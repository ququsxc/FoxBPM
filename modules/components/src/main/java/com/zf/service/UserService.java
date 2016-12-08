package com.zf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zf.dao.UserDao;
import com.zf.model.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;

	public User user(String id) {
		return userDao.user(id);
	}
}
