/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base integration test class for LmsApplication.
 *
 * Verifies that the Spring ApplicationContext loads successfully and that all
 * configurations (database, redis, security) are correctly initialized.
 */
@SpringBootTest
class LmsApplicationTests {

    /**
     * Test case to verify that the application context starts up without errors.
     */
    @Test
    void contextLoads() {
        // Assert that context loads without throwing exceptions.
    }

}

