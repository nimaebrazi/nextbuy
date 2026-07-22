package com.nextbuy.adhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith(sharedModules = "shared")
@SpringBootApplication
public class AdhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdhubApplication.class, args);
	}

}
