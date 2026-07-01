package org.example.miniproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MiniprojectApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring application context starts up successfully
        // with all controllers, services, and repositories wired correctly.
    }

}
