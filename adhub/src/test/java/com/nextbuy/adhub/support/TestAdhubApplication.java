package com.nextbuy.adhub.support;

import com.nextbuy.adhub.AdhubApplication;
import org.springframework.boot.SpringApplication;

public class TestAdhubApplication {

	public static void main(String[] args) {
		SpringApplication.from(AdhubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
