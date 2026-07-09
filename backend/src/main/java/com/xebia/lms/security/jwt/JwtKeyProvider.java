/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.jwt;

import com.xebia.lms.config.AppProperties;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Component responsible for loading and parsing RSA cryptographic keys.
 *
 * It reads PEM-formatted files from the configured paths (supporting classpath
 * and external file system locations) and parses them into Java Security Key objects
 * for RS256 JWT operations.
 */
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class JwtKeyProvider {

    private final AppProperties appProperties;
    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Initializes the cryptographic keys on application startup.
     * Loads the private and public keys configured in application properties.
     *
     * @throws IllegalStateException if keys cannot be loaded or parsed
     */
    @PostConstruct
    public void init() {
        try {
            log.info("Loading cryptographic keys for RS256 token signing/verification...");
            
            AppProperties.Security.Jwt jwtProps = appProperties.getSecurity().getJwt();
            
            byte[] privateKeyBytes = readPemFile(jwtProps.getPrivateKeyPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            byte[] publicKeyBytes = readPemFile(jwtProps.getPublicKeyPath());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
            
            log.info("Cryptographic keys successfully loaded and parsed.");
        } catch (Exception e) {
            log.error("Failed to load or parse cryptographic keys", e);
            throw new IllegalStateException("Failed to initialize security key providers", e);
        }
    }

    /**
     * Reads a PEM file, strips the BEGIN/END headers, collapses whitespaces, and base64-decodes the DER content.
     *
     * @param path target location of the key file (e.g. classpath:keys/private.pem)
     * @return decoded binary key representation
     * @throws Exception if reading or decoding fails
     */
    private byte[] readPemFile(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            // Remove PEM header/footer lines and all newline/carriage-return characters.
            String cleaned = content
                .replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
            return Base64.getDecoder().decode(cleaned);
        }
    }
}

