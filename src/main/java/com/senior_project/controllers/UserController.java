package com.senior_project.controllers;


import com.senior_project.dto.*;
import com.senior_project.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "APIs for managing users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private ResponseEntity<UUID> getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        UUID userId = userService.getUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users for admins")
    @PreAuthorize("hasRole(\"ADMIN\")")
    public ResponseEntity<Map<String, List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(Map.of("users", userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(\"ADMIN\")")
    @Operation(summary = "Get user profile by id for admins")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable UUID id) {
        UserResponse userResponse = userService.getUserProfile(id);
        if (userResponse == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(Map.of("user", userResponse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile for admins by email")
    @PreAuthorize("hasRole(\"ADMIN\")")
    public ResponseEntity<Map<String, UserResponse>> updateUserProfile(@PathVariable UUID id, @RequestBody UserResponse userResponse) {
        UserResponse updatedUser = userService.updateUserProfile(id, userResponse);
        return ResponseEntity.ok(Map.of("updatedUser", updatedUser));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user by ID for admins")
    @PreAuthorize("hasRole(\"ADMIN\")")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully!"));
    }

    @PutMapping("/update")
    @Operation(summary = "Update user profile for authenticated users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody UserResponse userResponse, Principal principal) {
        ResponseEntity<UUID> userIdResponse = getUserIdFromPrincipal(principal);
        if (!userIdResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        UUID userId = userIdResponse.getBody();
        UserResponse updatedUser = userService.updateUserProfile(userId, userResponse);
        return ResponseEntity.ok(Map.of("updatedUser", updatedUser));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile for authenticated users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getProfile(Principal principal) {
        ResponseEntity<UUID> userIdResponse = getUserIdFromPrincipal(principal);
        if (!userIdResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        UUID userId = userIdResponse.getBody();
        UserResponse userResponse = userService.getUserProfile(userId);
        return ResponseEntity.ok(Map.of("user", userResponse));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        ResponseEntity<UUID> userIdResponse = getUserIdFromPrincipal(principal);
        if (!userIdResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        UUID userId = userIdResponse.getBody();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully!"));
    }


    //TODO: Implement the changePassword method for admins (think about the case)


//    @PostMapping("/forgot-password")
//    @Operation(summary = "Initiate password reset")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
//        userService.forgotPassword(request.getEmail());
//        return ResponseEntity.ok("Password reset email sent!");
//    }
//
//
//    @PostMapping("/reset-password")
//    @Operation(summary = "Reset password")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
//        userService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
//        return ResponseEntity.ok("Password reset successful!");
//    }

}
