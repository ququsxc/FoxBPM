package org.foxbpm.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;
import org.foxbpm.engine.impl.entity.GroupEntity;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.service.ExpenseService;
import org.foxbpm.rest.common.api.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/expense")
public class ExpenseRestController extends AbstractController {
	Logger log = LoggerFactory.getLogger(ExpenseController.class);

	@Autowired
	private ExpenseService expenseService;

	@RequestMapping("/initAdd")
	@ResponseBody
	public RestResult initAdd(@RequestParam String loginToken, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		map.put("expenseId", "BXD-" + DateFormatUtils.format(new Date(), "yyyyMMddhhmmsssss"));

		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		UserEntity user = Authentication.selectUserByUserId(userId);
		map.put("owner", user.getUserId());
		map.put("ownerName", user.getUserName());
		List<GroupEntity> groups = user.getGroups();
		List<Map<String, Object>> groupList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(groups)) {
			for (GroupEntity group : groups) {
				if ("dept".equals(group.getGroupType())) {
					Map<String, Object> groupMap = new HashMap<>();
					groupMap.put("groupId", group.getGroupId());
					groupMap.put("groupName", group.getGroupName());
					groupList.add(groupMap);
				}
			}
		}
		map.put("dept", groupList);
		return RestResult.success(map);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public RestResult applyExpense(HttpServletRequest request, ExpenseEntity expenseEntity, @RequestParam String loginToken) {
		try {
			Map<String, Object> formData = getFormData(request);
			String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);

			expenseEntity.setOwner(userId);
			expenseService.applyNewExpense(expenseEntity, formData);
			return RestResult.success("启动成功！");
		} catch (Exception ex) {
			log.error("报销流程启动失败！", ex);
			return RestResult.fail("启动失败，原因:" + ex.getMessage());
		}

	}

	@RequestMapping(value = "/find/{expenseId}", method = RequestMethod.GET)
	@ResponseBody
	public RestResult getExpenseById(@PathVariable String expenseId, @RequestParam String loginToken) {
		ExpenseEntity expenseEntity = expenseService.selectExpenseById(expenseId);
		return RestResult.success(expenseEntity);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateExpense(HttpServletRequest request, ExpenseEntity expenseEntity, @RequestParam String loginToken) {
		try {
			Map<String, Object> formData = getFormData(request);
			expenseService.updateExpense(expenseEntity, formData);
			return RestResult.success("更新成功！");
		} catch (Exception e) {
			log.error("更新失败！", e);
			return RestResult.fail("更新失败，原因:" + e.getMessage());
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public DataResult getExpenseByPage(@RequestParam String loginToken, @RequestParam int start, @RequestParam int length) {
		return expenseService.selectByPage(start, length);
	}
}
