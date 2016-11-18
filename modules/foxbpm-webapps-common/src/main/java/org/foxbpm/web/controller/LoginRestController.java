package org.foxbpm.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.util.StringUtil;
import org.foxbpm.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.rong.models.TokenReslut;
import jodd.util.Base64;

@Controller
@RequestMapping("/rest")
public class LoginRestController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private RestService restService;

	@Value("${URL_RONG_CLOUD}")
	private String urlRongCloud;

	@Value("${URL_BPM_PORTAL}")
	private String urlBpmPortal;

	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public RestResult doLogin(HttpServletRequest request, HttpSession session) {
		try {
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			Map<String, Object> map = new HashMap<>();
			UserEntity userEntity = (UserEntity) Authentication.selectUserByUserId(userName);
			if (null != userEntity && StringUtil.equals(password, userEntity.getPassword())) {
				// 获取UserToken
				TokenReslut userToken = restService.getUserToken(userName, userEntity.getUserName(), userEntity.getImage());
				if (userToken.getCode().equals(200)) {
					map.put("userToken", userToken.getToken());
				} else {
					return RestResult.fail(userToken.getErrorMessage());
				}

				map.put("userId", userName);
				map.put("userName", userEntity.getUserName());

				// 这里约定了一个参数，流程引擎在运行时会默认从session里按照这两个key来获取参数，如果替换了登录的方式，请保证这两个key依然可以获取到正确的数据
				// session.setAttribute("userId", userEntity.getUserId());
				// session.setAttribute("user", userEntity);

				String targetUrl = "portal.html";
				// if ("1".equals(target)) {
				// targetUrl = "manage-index.html";
				// } else if ("2".equals(target)) {
				// targetUrl = "governance-index.html";
				// }
				// Cookie cookie = new Cookie("foxSid",
				// userEntity.getUserId());
				// cookie.setMaxAge(-1);
				// response.addCookie(cookie);

				// 生成base 64位验证码
				// String base64Code = "Basic " +
				// Base64.encodeToString(userEntity.getUserId() + ":" +
				// userEntity.getPassword());
				// map.put("BASE_64_CODE", base64Code);

				// Cookie userIdCookie = new Cookie("userId",
				// userEntity.getUserId());
				// userIdCookie.setMaxAge(-1);
				// response.addCookie(userIdCookie);

				String loginToken = Base64.encodeToString(userEntity.getUserId() + ":" + userEntity.getPassword() + ":" + new Date().getTime());
				Constants.LOGIN_TOKEN_CACHE.add(loginToken, userEntity.getUserId());
				map.put("loginToken", loginToken);

				map.put("url", targetUrl);
				return RestResult.success(map);
			} else {
				throw new RuntimeException(String.format("登录失败！用户名或密码错误！", userName, password));
			}
		} catch (Exception e) {
			LOG.error("发生异常", e);
			return RestResult.fail(e.getMessage());
		}
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	@ResponseBody
	public RestResult logout(HttpServletRequest request, HttpServletResponse response, @RequestParam String loginToken) {
		request.getSession().invalidate();
		Constants.LOGIN_TOKEN_CACHE.remove(loginToken);
		return RestResult.success();
	}

}
