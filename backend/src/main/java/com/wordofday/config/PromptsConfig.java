package com.wordofday.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:prompts.yml", factory = YamlPropertySourceFactory.class)
public class PromptsConfig {
}