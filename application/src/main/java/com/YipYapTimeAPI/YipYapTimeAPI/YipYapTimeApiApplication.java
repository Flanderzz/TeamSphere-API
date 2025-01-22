package com.YipYapTimeAPI.YipYapTimeAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class YipYapTimeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(YipYapTimeApiApplication.class, args);
	}

	// TODO: Move Beans to configuration files
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
