package org.foxbpm.rest.pojo;

public class Group {
	private String groupId;
	private String groupName;
	private String portraitUri;

	public Group() {
	}

	public Group(String groupId, String groupName, String portraitUri) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.portraitUri = portraitUri;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPortraitUri() {
		return portraitUri;
	}

	public void setPortraitUri(String portraitUri) {
		this.portraitUri = portraitUri;
	}
}
