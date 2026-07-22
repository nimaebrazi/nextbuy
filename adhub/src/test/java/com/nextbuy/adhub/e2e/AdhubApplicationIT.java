package com.nextbuy.adhub.e2e;

import com.nextbuy.adhub.support.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@DisplayName("e2e:AdhubApplicationIT")
class AdhubApplicationIT {

    @Test
    @DisplayName("loads Spring application context")
    void contextLoads() {
    }
}
