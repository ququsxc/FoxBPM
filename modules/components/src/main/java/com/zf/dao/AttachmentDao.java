package com.zf.dao;

import java.util.List;
import java.util.Map;

import com.zf.model.Attachment;

public interface AttachmentDao {
	int save(Attachment attachment);

	int remove(String id);

	Attachment get(String id);

	void updateToken(Map<String, Object> param);

	List<Attachment> findByToken(String token);
}
