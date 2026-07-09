/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.service.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of {@link EmailSender} that logs email actions to the system console.
 *
 * Implements the temporary email transmission requirements during Phase 1. Will be extended
 * or replaced with real SMTP/SES implementations in Phase 4.
 */
@Slf4j
@Service
public class LoggingEmailSender implements EmailSender {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("------------------------------------------------------------");
        log.info("MOCK EMAIL SENT:");
        log.info("To:      {}", to);
        log.info("Subject: {}", subject);
        log.info("Body:    {}", body);
        log.info("------------------------------------------------------------");
    }
}

