package com.nextbuy.adhub.location.infrastructure.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nextbuy.adhub")
public class ListingCliApplication {

    public static void main(String[] args) {
        var app = new SpringApplication(ListingCliApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setAdditionalProfiles("cli");
        System.exit(SpringApplication.exit(app.run(args)));
    }
}
