/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Xebia LMS application.
 *
 * This module initializes and runs the platform's core Identity and Access
 * Management (IAM) service. It serves as the foundation for the LMS ecosystem,
 * providing authentication, dynamic RBAC, user management, and other common platform utilities.
 */
@SpringBootApplication
@EnableScheduling
public class LmsApplication {

    /**
     * Standard main method to bootstrap the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }

}

