package com.zf.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;

@Conditional(LinuxEnv.LinuxCondition.class)
@Service
public class LinuxEnv implements Env {

	@Value("${UPLOAD_DIR_LINUX}")
	private String uploadDir;

	@Override
	public String getUploadDir() {
		return uploadDir;
	}

	private static class LinuxCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().getProperty("os.name").contains("Linux");
		}

	}
}
