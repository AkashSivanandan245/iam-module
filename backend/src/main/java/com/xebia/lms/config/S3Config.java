/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/**
 * Configuration class for the AWS S3 Client.
 *
 * Exposes a bean for software.amazon.awssdk.services.s3.S3Client used by
 * other platform services to upload and retrieve object files (such as user avatars,
 * training materials, course catalog assets, etc.).
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AppProperties appProperties;

    /**
     * Instantiates and configures the AWS S3Client bean.
     *
     * Business Rules:
     * - Access and secret keys are mapped from configuration properties.
     * - Enables overriding endpoint URIs to allow local development configurations
     *   (such as using LocalStack or MinIO instead of AWS production S3).
     *
     * @return the configured S3Client instance
     */
    @Bean
    public S3Client s3Client() {
        AppProperties.S3 s3Props = appProperties.getS3();

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            s3Props.getAccessKey(),
            s3Props.getSecretKey()
        );

        S3ClientBuilder builder = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(s3Props.getRegion()));

        // If an endpoint override is specified (e.g. MinIO or LocalStack),
        // configure S3Client to route request to the custom host.
        if (StringUtils.hasText(s3Props.getEndpoint())) {
            builder.endpointOverride(URI.create(s3Props.getEndpoint()));
            builder.forcePathStyle(true); // Required for custom non-DNS S3 configurations
        }

        return builder.build();
    }
}

