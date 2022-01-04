package com.alevikzs.springmultiplecronschedulers.configs;

import com.alevikzs.springmultiplecronschedulers.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class PropertiesConfig {
}
