package com.moneymanagerproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class MoneymanagerprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymanagerprojectApplication.class, args);
	}

}
