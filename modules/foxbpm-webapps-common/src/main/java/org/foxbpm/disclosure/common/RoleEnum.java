package org.foxbpm.disclosure.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.foxbpm.engine.impl.entity.GroupEntity;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;

public enum RoleEnum {
	OWNER, WORKER, DESIGNER, SUPERVISOR;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	private static final Map<String, List<String>> ROLE_ATTACHMENT_MAP = new HashMap<>();
	static {
		ROLE_ATTACHMENT_MAP.put(OWNER.toString(), Arrays.asList("constructionPermit", "constructionPass", "depositBill"));
		ROLE_ATTACHMENT_MAP.put(WORKER.toString(), Arrays.asList("houseStateTable", "constructionPlanTable"));
		ROLE_ATTACHMENT_MAP.put(DESIGNER.toString(), Arrays.asList("designDrawing"));
		ROLE_ATTACHMENT_MAP.put(SUPERVISOR.toString(), Arrays.asList("supervisionPlanTable"));
	}

	public static String getRole(String userId) {
		UserEntity user = Authentication.selectUserByUserId(userId);
		List<GroupEntity> groups = user.getGroups();
		for (GroupEntity g : groups) {
			if ("role".equals(g.getGroupType())) {
				return g.getGroupId();
			}
		}
		return null;
	}

	public static List<String> getAvailableAttachmentFields(String userId, boolean hasDesigner, boolean hasSupervisor) {
		String role = getRole(userId);
		List<String> list = new ArrayList<>();
		list.addAll(ROLE_ATTACHMENT_MAP.get(role));
		if (role.equals(WORKER.toString())) {
			if (!hasDesigner) {
				list.addAll(ROLE_ATTACHMENT_MAP.get(DESIGNER.toString()));
			}
			if (!hasSupervisor) {
				list.addAll(ROLE_ATTACHMENT_MAP.get(SUPERVISOR.toString()));
			}
		}
		return list;
	}

	public static List<String> getAllAttachmentFields() {
		List<String> list = new ArrayList<>();
		for (String key : ROLE_ATTACHMENT_MAP.keySet()) {
			list.addAll(ROLE_ATTACHMENT_MAP.get(key));
		}
		return list;
	}
}
