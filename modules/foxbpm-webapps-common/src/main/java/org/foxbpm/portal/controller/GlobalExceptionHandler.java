package org.foxbpm.portal.controller;

import org.foxbpm.common.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler
	@ResponseBody
	public RestResult handleException(Exception e) {
		LOG.error("系统异常！", e);
		return RestResult.fail("系统异常！" + e.getMessage());
	}
}
