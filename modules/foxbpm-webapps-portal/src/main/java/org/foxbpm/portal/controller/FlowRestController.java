package org.foxbpm.portal.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;
import org.foxbpm.engine.ModelService;
import org.foxbpm.engine.RuntimeService;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.runningtrack.RunningTrack;
import org.foxbpm.engine.runningtrack.RunningTrackQuery;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.model.ProcessTrack;
import org.foxbpm.portal.model.TodoTask;
import org.foxbpm.portal.service.ExpenseService;
import org.foxbpm.rest.common.api.DataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import scala.languageFeature.existentials;

@Controller
@RequestMapping("/rest/process")
public class FlowRestController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ExpenseService expenseService;

	@RequestMapping(value = "/definitions", method = RequestMethod.GET)
	@ResponseBody
	public RestResult definitions(@RequestParam String loginToken, UriComponentsBuilder ucb) {
		DataResult result = restTemplate.getForObject(ucb.scheme("http").path("/service/model/process-definitions").build().toUriString(), DataResult.class);
		return RestResult.success(result.getData());
	}

	@RequestMapping(value = "/tasks", method = RequestMethod.POST)
	@ResponseBody
	public RestResult tasks(@RequestParam String loginToken, @RequestParam int start, @RequestParam int length, @RequestParam(value = "search", required = false) String search) {
		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		List<TodoTask> tasks = expenseService.findTasks(userId, search, start, length);
		return RestResult.success(tasks);
	}

	@RequestMapping(value = "/tasks/{expenseId}", method = RequestMethod.GET)
	@ResponseBody
	public RestResult tasks(@RequestParam String loginToken, @PathVariable String expenseId) {
		TodoTask task = expenseService.findTaskDetail(expenseId);
		return RestResult.success(task);
	}

	@RequestMapping(value = "/assignedTrack", method = RequestMethod.POST)
	@ResponseBody
	public RestResult assignedTrack(@RequestParam String loginToken, @RequestParam int start, @RequestParam int length, @RequestParam(value = "search", required = false) String search) {
		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		List<ProcessTrack> assignedTracks = expenseService.findAssignedTrack(userId, search, start, length);
		return RestResult.success(assignedTracks);
	}

	@RequestMapping(value = "/initiatedTrack", method = RequestMethod.POST)
	@ResponseBody
	public RestResult initiatedTrack(@RequestParam String loginToken, @RequestParam int start, @RequestParam int length, @RequestParam(value = "search", required = false) String search) {
		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		List<ProcessTrack> initiatedTracks = expenseService.findInitiatedTrack(userId, search, start, length);
		return RestResult.success(initiatedTracks);
	}

	@RequestMapping(value = "/tracks/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
	public RestResult tracks(@RequestParam String loginToken, @PathVariable String processInstanceId) {
		ProcessTrack track = expenseService.findTrackDetail(processInstanceId);
		return RestResult.success(track);
	}
}
