package org.foxbpm.disclosure.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.common.Constants;
import org.foxbpm.common.RestResult;
import org.foxbpm.disclosure.common.RoleEnum;
import org.foxbpm.disclosure.dao.DisclosureDao;
import org.foxbpm.disclosure.model.Disclosure;
import org.foxbpm.engine.impl.entity.GroupEntity;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.util.ConvertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DisclosureService {
	@Autowired
	private DisclosureDao disclosureDao;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${URL_FILE_TOKEN}")
	private String URL_FILE_TOKEN;

	public int save(Disclosure disclosure) {
		return disclosureDao.save(disclosure);
	}

	public Map<String, Object> getCommonInfo(String id) {
		Disclosure commonInfo = disclosureDao.getCommonInfo(id);
		Map<String, Object> map = ConvertUtil.beanToMap(commonInfo);
		getAttachments(Arrays.asList("housePicture"), map);
		return map;
	}

	public int update(Disclosure disclosure) {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isNotEmpty(disclosure.getConstructionPermit())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getConstructionPermit());
			disclosure.setConstructionPermit(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getConstructionPass())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getConstructionPass());
			disclosure.setConstructionPass(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getDepositBill())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getDepositBill());
			disclosure.setDepositBill(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getHouseStateTable())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getHouseStateTable());
			disclosure.setHouseStateTable(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getConstructionPlanTable())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getConstructionPlanTable());
			disclosure.setConstructionPlanTable(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getSupervisionPlanTable())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getSupervisionPlanTable());
			disclosure.setSupervisionPlanTable(uuid);
		}
		if (StringUtils.isNotEmpty(disclosure.getDesignDrawing())) {
			String uuid = UUID.randomUUID().toString();
			map.put(uuid, disclosure.getDesignDrawing());
			disclosure.setDesignDrawing(uuid);
		}
		RestResult result = restTemplate.postForObject(URL_FILE_TOKEN, map, RestResult.class);
		if (result.getCode() == 0) {
			return disclosureDao.update(disclosure);
		} else {
			throw new RuntimeException(result.getMessage());
		}
	}

	public Map<String, Object> get(String id, String loginToken) {
		Disclosure disclosure = disclosureDao.get(id);
		String userId = Constants.LOGIN_TOKEN_CACHE.get(loginToken);
		Map<String, Object> map = ConvertUtil.beanToMap(disclosure);
		getAttachments(Arrays.asList("housePicture"), map);

		List<String> attachmentFields = RoleEnum.getAvailableAttachmentFields(userId, disclosure.getDesignerId() != null, disclosure.getSupervisorId() != null);
		getAttachments(attachmentFields, map);

		map.put("role", RoleEnum.getRole(userId));

		return map;
	}

	public Map<String, Object> getForConfirm(String id, String loginToken) {
		Disclosure disclosure = disclosureDao.get(id);
		Map<String, Object> map = ConvertUtil.beanToMap(disclosure);

		getAttachments(Arrays.asList("housePicture"), map);

		List<String> attachmentFields = RoleEnum.getAllAttachmentFields();
		getAttachments(attachmentFields, map);
		return map;
	}

	private void getAttachments(List<String> attachmentFields, Map<String, Object> map) {
		for (String field : attachmentFields) {
			String token = (String) map.get(field);
			if (StringUtils.isNotEmpty(token)) {
				RestResult result = restTemplate.getForObject(URL_FILE_TOKEN + "/{token}", RestResult.class, token);
				if (result.getCode() == 0) {
					map.put(field, result.getData());
				} else {
					throw new RuntimeException(result.getMessage());
				}
			}
		}
	}

}
