package org.foxbpm.extend.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.foxbpm.engine.identity.UserDefinition;
import org.foxbpm.engine.impl.Context;
import org.foxbpm.engine.impl.db.ListQueryParameterObject;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.springframework.web.client.RestTemplate;

public class UserDefinitionRestImpl implements UserDefinition {

	private RestTemplate restTemplate;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public UserEntity findUserById(String userId) {
		return restTemplate.getForObject("", UserEntity.class);
	}

	@SuppressWarnings("unchecked")

	public List<UserEntity> findUsers(String idLike, String nameLike) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (idLike != null) {
			map.put("userId", idLike);
		}
		if (nameLike != null) {
			map.put("userName", nameLike);
		}
		return (List<UserEntity>) Context.getCommandContext().getSqlSession().selectList("selectUsers", map);
	}

	@SuppressWarnings("unchecked")

	public List<UserEntity> findUsers(String idLike, String nameLike, int pageIndex, int pageSize) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		if (idLike != null) {
			queryMap.put("userId", idLike);
		}
		if (nameLike != null) {
			queryMap.put("userName", nameLike);
		}
		int firstResult = pageIndex * pageSize - pageSize;
		int maxResults = pageSize;
		ListQueryParameterObject queryParams = new ListQueryParameterObject(queryMap, firstResult, maxResults);
		return (List<UserEntity>) Context.getCommandContext().getSqlSession().selectList("selectUsersByPage", queryParams);
	}

	public Long findUserCount(String idLike, String nameLike) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		if (idLike != null) {
			queryMap.put("userId", idLike);
		}
		if (nameLike != null) {
			queryMap.put("userName", nameLike);
		}
		return (Long) Context.getCommandContext().getSqlSession().selectOne("selectUsersCount", queryMap);
	}

	public void updateUser(UserEntity user) {
		Context.getCommandContext().getSqlSession().update("updateUser", user);
	}

	public void addUser(UserEntity user) {
		Context.getCommandContext().getSqlSession().insert("insertUser", user);
	}

	public void deleteUser(String userId) {
		Context.getCommandContext().getSqlSession().delete("insertUser", userId);
	}
}
