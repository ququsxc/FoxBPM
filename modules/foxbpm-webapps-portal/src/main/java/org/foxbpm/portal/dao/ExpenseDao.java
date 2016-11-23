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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.portal.model.ExpenseEntity;
import org.foxbpm.portal.model.Task;
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

	private static final RowMapper<Task> TASK_ROW_MAPPER = new BeanPropertyRowMapper<>(Task.class);

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

	public List<Task> findTasks(String assignee, String search, int start, int length) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.id taskId,a.processinstance_id processInstanceId,a.processdefinition_id processDefinitionId,a.processdefinition_key processDefinitionKey,a.subject,b.invoiceType,a.process_initiator initiator,a.processstart_time startTime,b.id expenseId FROM foxbpm_run_task a,tb_expense b WHERE b.id=a.bizkey").append(" AND a.assignee=?").append(" AND a.end_time IS NULL");
		List<Object> param = new ArrayList<>();
		param.add(assignee);
		if (StringUtils.isNotEmpty(search)) {
			sql.append(" AND (a.subject like ? or b.invoiceType like ? or b.id like ?)");
			String searchLike = "%" + search + "%";
			param.add(searchLike);
			param.add(searchLike);
			param.add(searchLike);
		}
		sql.append(String.format(" LIMIT %d,%d", start, length));

		List<Task> resultList = jdbcTemplate.query(sql.toString(), param.toArray(), TASK_ROW_MAPPER);
		return resultList;
	}

	public Task findTaskDetail(String expenseId) {
		String sql = "SELECT a.id taskId,a.processinstance_id processInstanceId,a.processdefinition_id processDefinitionId,a.processdefinition_key processDefinitionKey,a.subject,b.invoiceType,a.process_initiator initiator,a.processstart_time startTime,b.id expenseId FROM foxbpm_run_task a,tb_expense b WHERE b.id=a.bizkey and b.id=? AND a.end_time IS NULL";
		Task task = jdbcTemplate.queryForObject(sql, TASK_ROW_MAPPER, expenseId);
		return task;
	}
}
