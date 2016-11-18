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
package org.foxbpm.web.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.Base64;

import org.foxbpm.engine.exception.FoxBPMException;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.util.StringUtil;
import org.foxbpm.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.rong.models.TokenReslut;

@Controller
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private RestService restService;

	@Value("${URL_RONG_CLOUD}")
	private String urlRongCloud;

	@Value("${URL_BPM_PORTAL}")
	private String urlBpmPortal;

	@RequestMapping(value = "login", method = { RequestMethod.GET, RequestMethod.POST })
	public void doLogin(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 从登录的口获取到用户名和密码
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			// 该接口同时也是登出的口，当发现有特殊参数时则做登出操作。
			String logout = request.getParameter("doLogOut");
			String contextPath = request.getContextPath();
			if (StringUtil.isNotEmpty(logout)) {
				UserEntity user = (UserEntity) request.getSession().getAttribute("user");
				request.getSession().invalidate();
				if (logout.equals("lock") && user != null) {

					Cookie cookie = new Cookie("userId", user.getUserId());
					cookie.setMaxAge(-1);
					response.addCookie(cookie);

					Cookie cookie2 = new Cookie("email", user.getEmail());
					cookie2.setMaxAge(-1);
					response.addCookie(cookie2);

					request.setAttribute("userId", user.getUserId());
					request.setAttribute("userName", user.getUserName());
					request.setAttribute("email", user.getEmail());
					response.sendRedirect(contextPath + "/lock.jsp");
				} else {
					response.sendRedirect(contextPath + "/login.html");
				}

			} else {
				UserEntity userEntity = (UserEntity) Authentication.selectUserByUserId(userName);
				if (null != userEntity && StringUtil.equals(password, userEntity.getPassword())) {
					request.getSession().setAttribute("urlRongCloud", urlRongCloud);
					request.getSession().setAttribute("urlBpmPortal", urlBpmPortal);

					// 这里约定了一个参数，流程引擎在运行时会默认从session里按照这两个key来获取参数，如果替换了登录的方式，请保证这两个key依然可以获取到正确的数据
					request.getSession().setAttribute("userId", userEntity.getUserId());
					request.getSession().setAttribute("user", userEntity);

					// 获取UserToken
					TokenReslut userToken = restService.getUserToken(userName, userEntity.getUserName(), userEntity.getImage());
					request.getSession().setAttribute("userToken", userToken);

					String target = request.getParameter("target");
					String targetUrl = "/portal/index.jsp";
					if ("1".equals(target)) {
						targetUrl = "/manage/index.html";
					} else if ("2".equals(target)) {
						targetUrl = "/governance/index.html";
					}
					// Cookie cookie = new Cookie("foxSid",
					// userEntity.getUserId());
					// cookie.setMaxAge(-1);
					// response.addCookie(cookie);

					// 生成base 64位验证码
					String base64Code = "Basic " + Base64.encodeToString(userEntity.getUserId() + ":" + userEntity.getPassword());
					request.getSession().setAttribute("BASE_64_CODE", base64Code);

					Cookie userIdCookie = new Cookie("userId", userEntity.getUserId());
					userIdCookie.setMaxAge(-1);
					response.addCookie(userIdCookie);

					response.sendRedirect(contextPath + targetUrl);
				} else {
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().print("<script>alert('用户名或密码错误！');window.location.href='" + contextPath + "/login.html';</script>");
					throw new RuntimeException(String.format("登录失败！用户名（%s）或密码（%s）错误！", userName, password));
				}
			}
		} catch (Exception e) {
			LOG.error("发生异常", e);
		}
	}
}
