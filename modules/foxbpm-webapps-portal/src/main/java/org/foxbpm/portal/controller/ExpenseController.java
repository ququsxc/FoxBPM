/**
 * Copyright 1996-2014 FoxBPM ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author ych
 */
package org.foxbpm.portal.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.util.StringUtil;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.service.ExpenseService;
import org.foxbpm.rest.common.api.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jodd.util.Base64;

@Controller
public class ExpenseController extends AbstractController {

	Logger log = LoggerFactory.getLogger(ExpenseController.class);
	@Autowired
	private ExpenseService expenseService;

	@RequestMapping(value = { "/", "/expenses" }, method = RequestMethod.POST)
	public void applyExpense(HttpServletResponse response, HttpServletRequest request, @ModelAttribute ExpenseEntity expenseEntity) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		try {
			Map<String, Object> formData = getFormData(request);
			UserEntity userEntity = (UserEntity) request.getSession().getAttribute("user");

			if (userEntity == null) {
				SimulateLogin(response, request); // 模拟登录，用于远程接口调用（见‘JavaRESTClient.java’）
				userEntity = (UserEntity) request.getSession().getAttribute("user");
			}

			expenseEntity.setOwner(userEntity.getUserId());
			expenseService.applyNewExpense(expenseEntity, formData);
			response.getWriter().print(showMessage("启动成功！", true));
		} catch (Exception ex) {
			log.error("报销流程启动失败！", ex);
			response.getWriter().print(showMessage("启动失败，原因:" + ex.getMessage(), false));
		}

	}

	@RequestMapping(value = { "/", "/updateExpense" }, method = RequestMethod.POST)
	public void updateExpense(HttpServletResponse response, HttpServletRequest request, @ModelAttribute ExpenseEntity expenseEntity) throws IOException {
		Map<String, Object> formData = getFormData(request);
		expenseService.updateExpense(expenseEntity, formData);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(showMessage("更新成功！", true));
	}

	@RequestMapping(value = { "/", "/findExpense" }, method = RequestMethod.GET)
	@ResponseBody
	public ExpenseEntity getExpenseById(@RequestParam String expenseId) {
		return expenseService.selectExpenseById(expenseId);
	}

	@RequestMapping(value = { "/", "/listExpense" }, method = RequestMethod.GET)
	@ResponseBody
	public DataResult getExpenseByPage(@RequestParam int pageIndex, @RequestParam int pageSize) {
		return expenseService.selectByPage(pageIndex, pageSize);
	}

	/**
	 * 模拟用户登录
	 * 
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	private void SimulateLogin(HttpServletResponse response, HttpServletRequest request) throws Exception {
		// 从登录的口获取到用户名和密码
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		// 该接口同时也是登出的口，当发现有特殊参数时则做登出操作。
		// String logout = request.getParameter("doLogOut");
		String contextPath = request.getContextPath();

		UserEntity userEntity = (UserEntity) Authentication.selectUserByUserId(userName);
		if (null != userEntity && StringUtil.equals(password, userEntity.getPassword())) {
			// 这里约定了一个参数，流程引擎在运行时会默认从session里按照这两个key来获取参数，如果替换了登录的方式，请保证这两个key依然可以获取到正确的数据
			request.getSession().setAttribute("userId", userEntity.getUserId());
			request.getSession().setAttribute("user", userEntity);

			// 生成base 64位验证码
			String base64Code = "Basic " + Base64.encodeToString(userEntity.getUserId() + ":" + userEntity.getPassword());
			request.getSession().setAttribute("BASE_64_CODE", base64Code);

			Cookie userIdCookie = new Cookie("userId", userEntity.getUserId());
			userIdCookie.setMaxAge(-1);
			response.addCookie(userIdCookie);

			// response.sendRedirect(contextPath + targetUrl);
		} else {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().print("<script>alert('用户名或密码错误！');window.location.href='" + contextPath + "/login.html';</script>");
		}

	}
}
