package com.gaziyev.microinstaclone.feedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeedServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedServiceApplication.class, args);
	}

}
