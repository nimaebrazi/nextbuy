package com.nextbuy.passport;

import org.springframework.boot.SpringApplication;

public class TestPassportApplication {

	public static void main(String[] args) {
		SpringApplication.from(PassportApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
