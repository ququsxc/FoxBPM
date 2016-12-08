package com.zf.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;

@Conditional(WindowsEnv.WindowsCondition.class)
@Service
public class WindowsEnv implements Env {

	@Value("${UPLOAD_DIR_WINDOWS}")
	private String uploadDir;

	@Override
	public String getUploadDir() {
		return uploadDir;
	}

	private static class WindowsCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().getProperty("os.name").contains("Windows");
		}

	}
}
