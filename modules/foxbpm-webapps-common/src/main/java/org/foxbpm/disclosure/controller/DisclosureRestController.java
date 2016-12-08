package org.foxbpm.disclosure.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.regexp.recompile;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;
import org.foxbpm.disclosure.model.CommandInfo;
import org.foxbpm.disclosure.model.Disclosure;
import org.foxbpm.disclosure.service.DisclosureService;
import org.foxbpm.engine.TaskService;
import org.foxbpm.engine.exception.FoxBPMException;
import org.foxbpm.engine.impl.entity.GroupEntity;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.task.command.ExpandTaskCommand;
import org.foxbpm.engine.impl.util.StringUtil;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.service.WorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/rest/disclosure")
public class DisclosureRestController {

	@Autowired
	private DisclosureService disclosureService;

	@Autowired
	private TaskService taskService;

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public RestResult update(Disclosure disclosure, CommandInfo commandInfo, @RequestParam String loginToken) {
		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		disclosureService.update(disclosure);

		ExpandTaskCommand expandTaskCommand = new ExpandTaskCommand();
		expandTaskCommand.setCommandType(commandInfo.getCommandType());
		expandTaskCommand.setTaskCommandId(commandInfo.getCommandId());
		expandTaskCommand.setInitiator(userId);
		expandTaskCommand.setTaskId(commandInfo.getTaskId());
		taskService.expandTaskComplete(expandTaskCommand, null);
		return RestResult.success("更新成功！");
	}

	@RequestMapping(value = "/{disclosureId}/common", method = RequestMethod.GET)
	@ResponseBody
	public RestResult getCommonInfo(@PathVariable String disclosureId, @RequestParam String loginToken) {
		Map<String, Object> map = disclosureService.getCommonInfo(disclosureId);
		return RestResult.success(map);
	}

	@RequestMapping(value = "/{disclosureId}", method = RequestMethod.GET)
	@ResponseBody
	public RestResult get(@PathVariable String disclosureId, @RequestParam String loginToken) {
		Map<String, Object> map = disclosureService.get(disclosureId, loginToken);
		return RestResult.success(map);
	}

	@RequestMapping(value = "/{disclosureId}/confirm", method = RequestMethod.GET)
	@ResponseBody
	public RestResult confirm(@PathVariable String disclosureId, @RequestParam String loginToken) {
		Map<String, Object> map = disclosureService.getForConfirm(disclosureId, loginToken);
		return RestResult.success(map);
	}
}
