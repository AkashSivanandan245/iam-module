/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security;

import com.xebia.lms.repository.AppUserRepository;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Component responsible for resolving user permissions with a versioned caching strategy.
 *
 * Utilizes a Redis Set cache with fallback to SQL database lookups, ensuring resilient
 * failover if the cache cluster is temporarily unreachable.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorityResolver {

    private final AppUserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * Resolves the effective authorities for a user given their unique identity
     * and current permissionVersion.
     *
     * Caching Pattern:
     * - Key format: perm:{userId}:v{permissionVersion}
     * - Cache Hit: Reads directly from Redis.
     * - Cache Miss: Loads permissions from DB, updates Redis Set, sets 24h expiration, and returns.
     * - Resiliency: Any Redis communication failure is caught and logged, falling back directly to SQL.
     *
     * @param userId user unique UUID
     * @param permissionVersion current permission version claim
     * @return list of SimpleGrantedAuthority permissions
     */
    public List<SimpleGrantedAuthority> resolveAuthorities(UUID userId, int permissionVersion) {
        String cacheKey = "perm:" + userId + ":v" + permissionVersion;
        
        // Step 1: Attempt Redis Cache lookup
        try {
            Set<String> cached = redisTemplate.opsForSet().members(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                log.debug("Cache hit for user {} permissions (version: {})", userId, permissionVersion);
                return cached.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            }
        } catch (Exception e) {
            log.error("Redis connection failed during permission query for user ID {}. Falling back to database.", userId, e);
        }

        log.debug("Cache miss for user {} permissions (version: {}). Loading from DB.", userId, permissionVersion);

        // Step 2: Fallback to SQL Database
        List<String> roleAuthorities = userRepository.findRoleAuthoritiesByUserId(userId);

        Set<String> effectivePermissions = new HashSet<>(roleAuthorities);

        // Step 3: Populate Redis cache asynchronously/safely
        try {
            if (!effectivePermissions.isEmpty()) {
                redisTemplate.opsForSet().add(cacheKey, effectivePermissions.toArray(new String[0]));
                redisTemplate.expire(cacheKey, Duration.ofHours(24));
                log.debug("Cached resolved permissions for user ID: {} under key: {}", userId, cacheKey);
            }
        } catch (Exception e) {
            log.error("Failed to populate Redis cache for user ID: {}", userId, e);
        }

        return effectivePermissions.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();
    }
}

