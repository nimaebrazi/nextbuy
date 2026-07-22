package com.nextbuy.passport;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Tags({ @Tag("integration"), @Tag("smoke") })
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PassportApplicationIT {

	@Test
	void contextLoads() {
	}

}
