package com.nextbuy.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PassportApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassportApplication.class, args);
	}

}
