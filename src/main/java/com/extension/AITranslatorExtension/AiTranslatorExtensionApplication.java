package com.extension.AITranslatorExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class AiTranslatorExtensionApplication {

	private static final Logger logger = LoggerFactory.getLogger(AiTranslatorExtensionApplication.class);

	public static void main(String[] args) {
		logger.info("Starting AI Translator Extension Backend...");
		var context = SpringApplication.run(AiTranslatorExtensionApplication.class, args);

		Environment env = context.getEnvironment();
		String port = env.getProperty("server.port", "8080");
		String appName = env.getProperty("spring.application.name", "AITranslatorExtension");

		logger.info("Application '{}' started successfully on port {}", appName, port);
		logger.info("Environment: {}", env.getActiveProfiles().length > 0 ?
				String.join(", ", env.getActiveProfiles()) : "default");
	}

}
