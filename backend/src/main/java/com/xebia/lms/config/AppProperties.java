/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Global configuration properties mapping for the Xebia LMS application.
 *
 * It maps values prefixed with "lms" defined in the application configuration files.
 * This class exposes properties for security configurations (JWT, OTP) and
 * S3 object storage settings.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "lms")
public class AppProperties {

    private final Security security = new Security();
    private final S3 s3 = new S3();
    private final RateLimit rateLimit = new RateLimit();

    /**
     * Grouping class for rate-limit configuration.
     * Applied by the Redis-backed RateLimitFilter to sensitive endpoints.
     */
    @Getter
    @Setter
    public static class RateLimit {
        // Global kill switch — useful for local dev where you don't want throttling.
        private boolean enabled = true;
        // Per-endpoint rules loaded from application.yml.
        private List<Rule> rules = new ArrayList<>();

        /**
         * A single rate-limit rule.
         *
         * name          — logical identifier used in log lines and Redis keys
         * pathPattern   — Ant-style pattern the rule applies to (e.g. /api/v1/auth/login)
         * limit         — maximum requests allowed inside the window
         * windowSeconds — window length in seconds (fixed window)
         */
        @Getter
        @Setter
        public static class Rule {
            private String name;
            private String pathPattern;
            private int limit;
            private int windowSeconds;
        }
    }

    /**
     * Grouping class for security-related properties (JWT and OTP).
     */
    @Getter
    @Setter
    public static class Security {
        private final Jwt jwt = new Jwt();
        private final Otp otp = new Otp();

        /**
         * Grouping class for JSON Web Token (JWT) configuration.
         */
        @Getter
        @Setter
        public static class Jwt {
            private String privateKeyPath;
            private String publicKeyPath;
            private int accessTokenExpirationMinutes = 15;
            private int refreshTokenExpirationDays = 7;
        }

        /**
         * Grouping class for One-Time Password (OTP) configuration.
         */
        @Getter
        @Setter
        public static class Otp {
            private int expirationMinutes = 5;
        }
    }

    /**
     * Grouping class for AWS S3 configuration.
     */
    @Getter
    @Setter
    public static class S3 {
        private String bucketName;
        private String region;
        private String accessKey;
        private String secretKey;
        private String endpoint;
    }
}

