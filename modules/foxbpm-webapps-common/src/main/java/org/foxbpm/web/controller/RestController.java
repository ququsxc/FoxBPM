package org.foxbpm.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foxbpm.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Controller
@RequestMapping(value = "/rest", method = RequestMethod.GET)
public class RestController {

	@Autowired
	RestService restService;

//	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
//	public UserInfo show(@PathVariable int id, HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//		return userService.getUserInfo(id);
//
//	}

//	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
//	public ListBean getAll() throws Exception {
//		return userService.getAllUsers();
//
//	}

	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public void show(@PathVariable int id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonObject jsonObject = new JsonObject();
		
		String str = "{expenseId:'BXD-0000000000000', createTime:'2018-08-08', ownerName:'马蓉'}";
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(str);

	}
}
