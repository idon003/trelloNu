package com.senior_project.services;

import com.senior_project.accounts.Role;
import com.senior_project.accounts.User;
import com.senior_project.dto.*;
import com.senior_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final EmailService emailService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials!");
        }
        return jwtTokenProvider.generateToken(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }


    public UserResponse getUserProfile(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhoneNumber(), user.getRole());
    }


    public UserResponse updateUserProfile(UUID id, UserResponse userResponse) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setPhoneNumber(userResponse.getPhoneNumber());

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhoneNumber(), user.getRole());
    }

    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect!");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
//        emailService.sendEmail(user.getEmail(), "Password changed", "Password changed successfully!");
//        notificationService.sendPasswordChangeNotification(user.getEmail());
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }

    public Role getRoleByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(User::getRole)
                .orElse(null);
    }


//    public void forgotPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", "ROLE_" + user.getRole());
//
//        String resetToken = jwtTokenProvider.generateTokenWithCustomClaims(user.getEmail(), claims);
//
//        if (resetToken == null) {
//            throw new IllegalStateException("Error generating reset token");
//        }
//        user.setResetToken(resetToken);
//
////        try {
////            String resetToken = jwtTokenProvider.generateTokenWithCustomClaims(user.getEmail(), claims);
////            user.setResetToken(resetToken);
////        } catch (Exception e) {
////            throw new IllegalStateException("Error generating reset token", e);
////        }
//
//
//        userRepository.save(user);
//
//        emailService.sendEmail(user.getEmail(), "Password reset request", resetToken);
//    }

//    public void sendPasswordResetEmail(String email, String token) {
//        String resetLink = "https://localhost:8080/reset-password?token=" + token;
//        String subject = "Password Reset Request";
//        String body = "Click the following link to reset your password: " + resetLink;
//
//        emailService.sendEmail(email, subject, body);
//    }

//    public void resetPassword(String token, String newPassword, String confirmPassword) {
//        String email = jwtTokenProvider.validateTokenAndGetEmail(token); // Extracts email from token.
//
//        if (!newPassword.equals(confirmPassword)) {
//            throw new IllegalArgumentException("Passwords do not match!");
//        }
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setResetToken(null);
//        userRepository.save(user);
//
//        notificationService.sendPasswordResetNotification(user.getEmail());
//    }
}
