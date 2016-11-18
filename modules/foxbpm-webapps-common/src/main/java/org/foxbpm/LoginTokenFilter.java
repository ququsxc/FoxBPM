package org.foxbpm;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;

import com.google.gson.Gson;

public class LoginTokenFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		if (request.getSession().getAttribute("userId") != null) {
			chain.doFilter(request, resp);
			return;
		}
		resp.setContentType("text/html;charset=UTF-8");
		if (!request.getRequestURI().endsWith("login.action")) {
			String loginToken = request.getParameter("loginToken");
			if (StringUtils.isEmpty(loginToken)) {
				resp.getWriter().println(Constants.GSON.toJson(RestResult.fail("缺少LoginToken参数！")));
				return;
			}
			String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
			if (StringUtils.isEmpty(userId)) {
				resp.getWriter().println(Constants.GSON.toJson(RestResult.fail(2, "LoginToken失效或尚未登录！")));
				return;
			}
			request.getSession().setAttribute("userId", userId);
		}
		chain.doFilter(request, resp);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
