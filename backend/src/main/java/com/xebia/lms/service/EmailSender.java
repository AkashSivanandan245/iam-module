/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

/**
 * Service interface for sending emails.
 *
 * Exposes a simple contract to transmit system notifications, password reset OTPs,
 * and user invitations.
 */
public interface EmailSender {

    /**
     * Sends an email notification to the specified recipient.
     *
     * Business Rules:
     * - Implementation must not block the main transaction thread (or can log asynchronously).
     * - Must sanitize parameters before transmission.
     * - Must never log passwords or raw secrets.
     *
     * @param to recipient email address
     * @param subject email subject line
     * @param body email content body (HTML or plain text)
     */
    void sendEmail(String to, String subject, String body);
}

