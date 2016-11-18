package org.foxbpm.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foxbpm.common.Constants;
import org.foxbpm.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import io.rong.models.GroupUserQueryReslut;

@Controller
@RequestMapping(value = "/rest", method = RequestMethod.GET)
public class RestController {

	@Autowired
	RestService restService;

	// @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	// public UserInfo show(@PathVariable int id, HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// return userService.getUserInfo(id);
	//
	// }

	// @RequestMapping(value = "/user/list", method = RequestMethod.GET)
	// public ListBean getAll() throws Exception {
	// return userService.getAllUsers();
	//
	// }

	// @RequestMapping(value = "/getUser/{id}", method = RequestMethod.GET)
	// public void show(@PathVariable int id, HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// JsonObject jsonObject = new JsonObject();

	// Gson gson = new Gson();
	// String json = gson.toJson(restService.getInstanceBiz());
	//
	// response.setContentType("text/html;charset=utf-8");
	// response.getWriter().print(json);

	// }

	/**
	 * 融云获取用户token
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/communication/user/getToken", method = RequestMethod.POST)
	public void getUserToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Gson gson = new Gson();
		String json = gson.toJson(restService.getUserToken(request.getParameter("userId"), request.getParameter("name"), request.getParameter("image")));

		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(json);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/communication/user/list", method = RequestMethod.GET)
	@ResponseBody
	public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return Constants.GSON.toJson(restService.data());
	}

	@RequestMapping(value = "/communication/group/create", method = RequestMethod.GET)
	public void createGroup() throws Exception {
		restService.createGroup();
	}

	@RequestMapping(value = "/communication/group/send", method = RequestMethod.GET)
	public void sendGroupMessage() throws Exception {
		restService.sendGroupMessage();
	}

	@RequestMapping(value = "/communication/group/{groupId}", method = RequestMethod.GET)
	@ResponseBody
	public String createGroup(@PathVariable String groupId) throws Exception {
		GroupUserQueryReslut groupUsers = restService.getGroupUsers(groupId);
		return Constants.GSON.toJson(groupUsers);
	}

}
