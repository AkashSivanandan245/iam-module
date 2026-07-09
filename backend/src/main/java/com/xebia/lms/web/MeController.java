/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.dto.user.UserPermissionsResponse;
import com.xebia.lms.security.CurrentUser;
import com.xebia.lms.security.LmsPrincipal;
import com.xebia.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for resolving current user contexts, independent of Auth scopes.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "Current User", description = "Endpoints for the active user context")
public class MeController {

    private final UserService userService;

    /**
     * Returns the resolved scopes/permissions for the UI.
     *
     * @param principal currently logged-in principal details
     * @return ResponseEntity containing UserPermissionsResponse DTO
     */
    @Operation(summary = "Get resolved scopes", description = "Retrieves the fully resolved permissions matrix for the UI.")
    @GetMapping("/permissions")
    public ResponseEntity<UserPermissionsResponse> getPermissions(@CurrentUser LmsPrincipal principal) {
        log.info("REST request to get resolved permissions for active user: {}", principal.getUserId());
        UserPermissionsResponse response = userService.getUserPermissions(principal.getUserId());
        return ResponseEntity.ok(response);
    }
}

