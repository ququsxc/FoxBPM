package com.zf.service;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zf.dao.AttachmentDao;
import com.zf.model.Attachment;

@Service
@Transactional
@CacheConfig(cacheNames = "attachmentsCache")
public class AttachmentService {
	@Autowired
	private AttachmentDao attachmentDao;

	public int save(Attachment attachment) {
		return attachmentDao.save(attachment);
	}

	public int remove(String id) {
		return attachmentDao.remove(id);
	}

	@Cacheable
	public Attachment get(String id) {
		return attachmentDao.get(id);
	}

	public void removeFromDiskAndDB(String id) {
		Attachment attachment = get(id);
		if (attachment != null) {
			File file = new File(attachment.getAbsolutePath());
			file.delete();
			remove(id);
		}
	}

	public void updateToken(Map<String, Object> map) {
		for (String key : map.keySet()) {
			map.put(key, Arrays.asList(((String) map.get(key)).split(",")));
		}
		attachmentDao.updateToken(map);
	}

	@Cacheable
	public List<Attachment> findByToken(String token) {
		return attachmentDao.findByToken(token);
	}
}
