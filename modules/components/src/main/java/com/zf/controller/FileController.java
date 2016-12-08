package com.zf.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.zf.common.FileEmptyException;
import com.zf.common.RestResult;
import com.zf.dao.AttachmentDao;
import com.zf.env.Env;
import com.zf.model.Attachment;
import com.zf.service.AttachmentService;

@RestController
@RequestMapping("/file")
public class FileController {

	public FileController() {
	}

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private Env env;

	@RequestMapping(method = RequestMethod.POST)
	public RestResult upload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws IllegalStateException, IOException {
		String uploadDir = env.getUploadDir();
		String todayStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
		List<String> list = new ArrayList<>();
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				throw new FileEmptyException();
			}
			String originalName = file.getOriginalFilename();
			String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
			String uuid = UUID.randomUUID().toString();
			String relativePath = File.separator + todayStr + File.separator + uuid + ext;
			String absolutePath = uploadDir + relativePath;
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(absolutePath));

			Attachment attachment = new Attachment();
			attachment.setId(uuid);
			attachment.setOriginalName(originalName);
			attachment.setContentType(file.getContentType());
			attachment.setAbsolutePath(absolutePath);
			attachment.setRelativePath(relativePath);
			attachment.setSize(file.getSize());
			attachmentService.save(attachment);
			list.add(uuid);
		}
		return RestResult.success("上传成功。", list);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public RestResult delete(@PathVariable String id) {
		attachmentService.removeFromDiskAndDB(id);
		return RestResult.success("删除成功。");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> download(@PathVariable String id, HttpServletRequest request, @RequestParam(required = false) String preview) throws IOException {
		Attachment attachment = attachmentService.get(id);
		File file = null;
		if (attachment == null || !(file = new File(attachment.getAbsolutePath())).exists()) {
			throw new FileNotFoundException();
		}
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(preview)) {
			headers.setContentType(MediaType.valueOf(attachment.getContentType()));
		} else {
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", URLEncoder.encode(attachment.getOriginalName(), "utf-8").replaceAll("\\+", "%20"));
		}
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}

	@ExceptionHandler
	@ResponseStatus
	public RestResult maxUploadSizeExceeded(MaxUploadSizeExceededException e) {
		SizeLimitExceededException cause = (SizeLimitExceededException) e.getCause();
		return RestResult.fail(String.format("文件内容过大（允许大小：%dKB，实际大小：%dKB）。", cause.getPermittedSize(), cause.getActualSize()));
	}

	@ExceptionHandler
	@ResponseStatus
	public RestResult fileEmpty(FileEmptyException e) {
		return RestResult.fail("文件不能为空。");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RestResult fileNotFound(FileNotFoundException e) {
		return RestResult.fail("文件不存在。");
	}

	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public RestResult updateToken(@RequestBody Map<String, Object> map) {
		if (!map.isEmpty()) {
			attachmentService.updateToken(map);
		}
		return RestResult.success("更新token成功！");
	}

	@RequestMapping(value = "/token/{token}", method = RequestMethod.GET)
	public RestResult findByToken(@PathVariable String token) {
		List<Attachment> list = attachmentService.findByToken(token);
		return RestResult.success(list);
	}

	@RequestMapping(value = "/{id}/image", method = RequestMethod.GET)
	public ResponseEntity<byte[]> image(@PathVariable String id) throws IOException {
		Attachment attachment = attachmentService.get(id);
		File file = null;
		if (attachment == null || !(file = new File(attachment.getAbsolutePath())).exists()) {
			throw new FileNotFoundException();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}

}
