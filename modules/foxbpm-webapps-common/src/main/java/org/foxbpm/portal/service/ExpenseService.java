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
package org.foxbpm.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.engine.RuntimeService;
import org.foxbpm.engine.TaskService;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.util.StringUtil;
import org.foxbpm.engine.runtime.ProcessInstance;
import org.foxbpm.engine.task.Task;
import org.foxbpm.portal.dao.ExpenseDao;
import org.foxbpm.portal.dao.ProcessDao;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.model.ProcessInfoEntity;
import org.foxbpm.portal.model.ProcessTrack;
import org.foxbpm.portal.model.TodoTask;
import org.foxbpm.rest.common.api.DataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpenseService {

	@Autowired
	private ExpenseDao expenseDao;
	@Autowired
	private ProcessDao processDao;
	@Autowired
	private WorkFlowService workFlowService;
	@Autowired
	private TaskService taskService;

	public void applyNewExpense(ExpenseEntity expenseEntity, Map<String, Object> formData) {
		expenseDao.saveExpenseEntity(expenseEntity);
		String flowCommandInfo = StringUtil.getString(formData.get("flowCommandInfo"));
		if (StringUtil.isEmpty(flowCommandInfo)) {
			throw new RuntimeException("流程命令参数缺失，请检查请求参数");
		}
		// 调用api执行任务命令
		ProcessInstance processInstance = (ProcessInstance) workFlowService.executeTaskCommandJson(formData);
		// 由于JPA此时并没有存储到数据库，所以在这里更新报销表中的流程实例编号字段,非JPA系统不需要下面代码
		if (processInstance != null) {
			ProcessInfoEntity processInfo = processDao.selectProcessInfoById(processInstance.getId());
			if (processInfo != null) {
				expenseEntity.setProcessInfo(processInfo);
			}
		}
	}

	public void updateExpense(ExpenseEntity expenseEntity, Map<String, Object> formData) {
		// expenseDao.updateExpenseEntity(expenseEntity);
		// 调用api执行任务命令
		workFlowService.executeTaskCommandJson(formData);
	}

	public DataResult selectByPage(int pageIndex, int pageSize) {
		List<ExpenseEntity> list = expenseDao.selectExpenseByPage(pageIndex, pageSize);
		int count = expenseDao.selectCount();
		DataResult dr = new DataResult();
		dr.setData(list);
		dr.setPageIndex(pageIndex);
		dr.setPageSize(pageSize);
		dr.setRecordsFiltered(count);
		dr.setRecordsTotal(count);
		dr.setTotal(count);
		return dr;
	}

	public ExpenseEntity selectExpenseById(String expenseId) {
		return expenseDao.selectExpenseById(expenseId);
	}

	public void save(ExpenseEntity expenseEntity) {
		expenseDao.saveExpenseEntity(expenseEntity);
	}

	public List<TodoTask> findTasks(String assignee, String search, int start, int length) {
		List<TodoTask> list = expenseDao.findTasks(assignee, search, start, length);
		return list;
	}

	public TodoTask findTaskDetail(String expenseId) {
		TodoTask task = expenseDao.findTaskDetail(expenseId);
		return task;
	}

	public List<ProcessTrack> findAssignedTrack(String assignee, String search, int start, int length) {
		List<ProcessTrack> assignedTracks = expenseDao.findAssignedTrack(assignee, search, start, length);
		setDetail(assignedTracks);
		return assignedTracks;
	}

	public List<ProcessTrack> findInitiatedTrack(String initiator, String search, int start, int length) {
		List<ProcessTrack> initiatedTracks = expenseDao.findInitiatedTrack(initiator, search, start, length);
		setDetail(initiatedTracks);
		return initiatedTracks;
	}

	private void setDetail(List<ProcessTrack> tracks) {
		for (ProcessTrack track : tracks) {
			if ("running".equals(track.getStatus())) {
				Task task = taskService.createTaskQuery().processInstanceId(track.getProcessInstanceId()).taskNotEnd().singleResult();
				Map<String, Object> map = new HashMap<>();
				map.put("assignee", task.getAssignee());
				if (StringUtils.isNotEmpty(task.getAssignee())) {
					map.put("assigneeName", Authentication.selectUserByUserId(task.getAssignee()).getUserName());
				}
				map.put("taskSubject", task.getSubject());
				track.setStatusDetail(map);
			}
		}
	}

	public ProcessTrack findTrackDetail(String processInstanceId) {
		ProcessTrack track = expenseDao.findTrackDetail(processInstanceId);
		return track;
	}
}
