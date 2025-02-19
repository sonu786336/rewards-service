package com.rewardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for the Rewards Service application.
 * This class contains the main method which is the entry point of the Spring Boot application.
 */
@SpringBootApplication
public class RewardsServiceApplication {

	/**
	 * The main method which starts the Spring Boot application.
	 *
	 * @param args command-line arguments passed during the application start
	 */
	public static void main(String[] args) {
		SpringApplication.run(RewardsServiceApplication.class, args);
	}

}
