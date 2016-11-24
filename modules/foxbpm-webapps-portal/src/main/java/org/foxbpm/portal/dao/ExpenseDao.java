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
package org.foxbpm.portal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.model.ProcessTrack;
import org.foxbpm.portal.model.TodoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component("expenseDao")
public class ExpenseDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final RowMapper<TodoTask> TASK_ROW_MAPPER = new BeanPropertyRowMapper<>(TodoTask.class);
	private static final RowMapper<ProcessTrack> TRACK_ROW_MAPPER = new BeanPropertyRowMapper<>(ProcessTrack.class);

	public void saveExpenseEntity(ExpenseEntity expenseEntity) {
		entityManager.persist(expenseEntity);
	}

	public void updateExpenseEntity(ExpenseEntity expenseEntity) {
		ExpenseEntity newObject = entityManager.find(ExpenseEntity.class, expenseEntity.getExpenseId());
		newObject.setAccount(expenseEntity.getAccount());
		newObject.setDept(expenseEntity.getDept());
		newObject.setCreateTime(expenseEntity.getCreateTime());
		newObject.setInvoiceType(expenseEntity.getInvoiceType());
		newObject.setReason(expenseEntity.getReason());
		newObject.setOwner(expenseEntity.getOwner());
		entityManager.flush();
	}

	public ExpenseEntity selectExpenseById(String entityId) {
		return entityManager.find(ExpenseEntity.class, entityId);
	}

	public List<ExpenseEntity> selectExpenseByPage(int start, int length) {
		String sql = "select expense from ExpenseEntity expense order by expense.createTime desc";
		List<ExpenseEntity> list = entityManager.createQuery(sql, ExpenseEntity.class).setFirstResult(start).setMaxResults(length).getResultList();
		return list;
	}

	public int selectCount() {
		CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> critQuery = critBuilder.createQuery(Long.class);
		Root<ExpenseEntity> root = critQuery.from(ExpenseEntity.class);
		critQuery.select(critBuilder.countDistinct(root));
		int count = entityManager.createQuery(critQuery).getSingleResult().intValue();
		return count;
	}

	private List<TodoTask> tasks(Map<String, Object> args) {
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<>();
		sql.append("SELECT a.id taskId,a.processinstance_id processInstanceId,a.processdefinition_id processDefinitionId,a.processdefinition_key processDefinitionKey,a.subject,b.invoiceType,a.process_initiator initiator,c.username initiatorName,a.processstart_time startTime,b.id expenseId FROM foxbpm_run_task a,tb_expense b,au_userinfo c WHERE b.id=a.bizkey AND a.process_initiator=c.userid AND a.end_time IS NULL");
		if (args.containsKey("expenseId")) {
			sql.append(" AND b.id=?");
			param.add(args.get("expenseId"));
		} else {
			if (args.containsKey("assignee")) {
				sql.append(" AND a.assignee=?");
				param.add(args.get("assignee"));
			}
			String search = (String) args.get("search");
			if (StringUtils.isNotEmpty(search)) {
				sql.append(" AND (c.username like ? or b.invoiceType like ? or b.id like ?)");
				String searchLike = "%" + search + "%";
				param.add(searchLike);
				param.add(searchLike);
				param.add(searchLike);
			}
			sql.append(String.format(" LIMIT %d,%d", (int) args.get("start"), (int) args.get("length")));
		}
		List<TodoTask> resultList = jdbcTemplate.query(sql.toString(), param.toArray(), TASK_ROW_MAPPER);
		return resultList;
	}

	/**
	 * 查询待办任务
	 * 
	 * @param assignee
	 * @param search
	 * @param start
	 * @param length
	 * @return
	 */
	public List<TodoTask> findTasks(String assignee, String search, int start, int length) {
		Map<String, Object> args = new HashMap<>();
		args.put("assignee", assignee);
		args.put("search", search);
		args.put("start", start);
		args.put("length", length);
		List<TodoTask> resultList = tasks(args);
		return resultList;
	}

	/**
	 * 待办任务详细
	 * 
	 * @param expenseId
	 * @return
	 */
	public TodoTask findTaskDetail(String expenseId) {
		Map<String, Object> args = new HashMap<>();
		args.put("expenseId", expenseId);
		List<TodoTask> resultList = tasks(args);
		return resultList.get(0);
	}

	private List<ProcessTrack> tracks(Map<String, Object> args) {
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<>();
		sql.append("SELECT a.id processInstanceId,a.processdefinition_id processDefinitionId,a.processdefinition_key processDefinitionKey,a.subject,b.invoiceType,a.initiator,c.username initiatorName,b.id expenseId,a.instance_status status,a.start_time startTime FROM foxbpm_run_processinstance a,tb_expense b,au_userinfo c");
		sql.append(" WHERE b.id=a.biz_key AND a.initiator=c.userid");
		if (args.containsKey("processInstanceId")) {
			sql.append(" AND a.id=?");
			param.add(args.get("processInstanceId"));
		} else {
			if (args.containsKey("assignee")) {
				sql.append(" AND EXISTS(SELECT * FROM foxbpm_run_runningtrack WHERE a.id=processinstance_id AND operator=?)");
				param.add(args.get("assignee"));
			} else if (args.containsKey("initiator")) {
				sql.append(" AND a.initiator=?");
				param.add(args.get("initiator"));
			}
			String search = (String) args.get("search");
			if (StringUtils.isNotEmpty(search)) {
				sql.append(" AND (c.username like ? or b.invoiceType like ? or b.id like ?)");
				String searchLike = "%" + search + "%";
				param.add(searchLike);
				param.add(searchLike);
				param.add(searchLike);
			}
			sql.append(String.format(" LIMIT %d,%d", (int) args.get("start"), (int) args.get("length")));
		}
		List<ProcessTrack> resultList = jdbcTemplate.query(sql.toString(), param.toArray(), TRACK_ROW_MAPPER);
		return resultList;
	}

	/**
	 * 查询流程追踪-已经办
	 * 
	 * @param assignee
	 * @param search
	 * @param start
	 * @param length
	 * @return
	 */
	public List<ProcessTrack> findAssignedTrack(String assignee, String search, int start, int length) {
		Map<String, Object> args = new HashMap<>();
		args.put("assignee", assignee);
		args.put("search", search);
		args.put("start", start);
		args.put("length", length);
		List<ProcessTrack> resultList = tracks(args);
		return resultList;
	}

	/**
	 * 查询流程追踪-已发起
	 * 
	 * @param initiator
	 * @param search
	 * @param start
	 * @param length
	 * @return
	 */
	public List<ProcessTrack> findInitiatedTrack(String initiator, String search, int start, int length) {
		Map<String, Object> args = new HashMap<>();
		args.put("initiator", initiator);
		args.put("search", search);
		args.put("start", start);
		args.put("length", length);
		List<ProcessTrack> resultList = tracks(args);
		return resultList;
	}

	/**
	 * 流程追踪详细
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public ProcessTrack findTrackDetail(String processInstanceId) {
		Map<String, Object> args = new HashMap<>();
		args.put("processInstanceId", processInstanceId);
		List<ProcessTrack> resultList = tracks(args);
		return resultList.get(0);
	}
}
