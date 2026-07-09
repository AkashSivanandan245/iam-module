/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security;

import java.lang.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Custom annotation to bind the currently authenticated principal ({@link LmsPrincipal})
 * to a controller handler method parameter.
 *
 * It is meta-annotated with {@link AuthenticationPrincipal} to utilize Spring Security's
 * automatic principal argument resolver.
 *
 * Example Usage:
 * <pre>
 * &#64;GetMapping("/me")
 * public MeResponse getProfile(&#64;CurrentUser LmsPrincipal principal) {
 *     return new MeResponse(principal.getUserId(), ...);
 * }
 * </pre>
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}

