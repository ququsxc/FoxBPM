package org.foxbpm.portal.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.foxbpm.engine.RuntimeService;
import org.foxbpm.engine.TaskService;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.impl.task.command.ExpandTaskCommand;
import org.foxbpm.engine.runtime.ProcessInstance;
import org.foxbpm.engine.task.Task;
import org.foxbpm.portal.CodeFlowMapping;
import org.foxbpm.portal.SpringContextHolder;
import org.foxbpm.portal.dao.ExpenseDao;
import org.foxbpm.portal.dao.ProcessDao;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.model.FlowMessage;
import org.foxbpm.portal.model.ProcessInfoEntity;

import com.google.gson.Gson;
import com.zf.kafka.KafkaConsumer;

public class ExpenseConsumer extends KafkaConsumer {

	private Gson gson = new Gson();
	private TaskService taskService;
	private RuntimeService runtimeService;
	private ProcessDao processDao;
	private ExpenseService expenseService;

	public ExpenseConsumer(String topic) {
		super(topic);
		this.taskService = SpringContextHolder.getBean(TaskService.class);
		this.runtimeService = SpringContextHolder.getBean(RuntimeService.class);
		this.processDao = SpringContextHolder.getBean(ProcessDao.class);
		this.expenseService = SpringContextHolder.getBean(ExpenseService.class);
	}

	@Override
	protected void handleMessage(String message) {
		FlowMessage flowMessage = gson.fromJson(message, FlowMessage.class);
		if (!flowMessage.isStartFlow()) {
			return;
		}
		String owner = "admin";
		ExpenseEntity expenseEntity = new ExpenseEntity();
		expenseEntity.setOwner(owner);
		Date today = new Date();
		String expenseId = "BXD-" + DateFormatUtils.format(today, "yyyyMMddhhmmsssss");
		expenseEntity.setExpenseId(expenseId);
		double account = 1;
		expenseEntity.setAccount(account);
		expenseEntity.setCreateTime(DateFormatUtils.format(today, "yyyy-MM-dd"));
		expenseEntity.setDept("101");
		expenseEntity.setInvoiceType("1");
		expenseEntity.setReason("xxx");

		Authentication.setAuthenticatedUserId(owner);

		String processDefinitionKey = CodeFlowMapping.get(flowMessage.getCode());
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskNotEnd().singleResult();

		ExpandTaskCommand expandTaskCommand = new ExpandTaskCommand();
		expandTaskCommand.setTaskId(task.getId());
		expandTaskCommand.setTaskCommandId("HandleCommand_3");
		expandTaskCommand.setCommandType("submit");
		expandTaskCommand.setBusinessKey(expenseEntity.getExpenseId());
		expandTaskCommand.addPersistenceVariable("account", String.valueOf(account));
		taskService.expandTaskComplete(expandTaskCommand, null);
		System.out.println("================启动流程成功================");

		ProcessInfoEntity processInfo = processDao.selectProcessInfoById(processInstance.getId());
		expenseEntity.setProcessInfo(processInfo);
		expenseService.save(expenseEntity);
	}

}
