package com.derinkaras.aimsense.controller;

import com.derinkaras.aimsense.dto.userProfile.CreateProfileRequest;
import com.derinkaras.aimsense.dto.userProfile.UpdateProfileRequest;
import com.derinkaras.aimsense.dto.userProfile.UserProfileDto;
import com.derinkaras.aimsense.service.GunProfileService;
import com.derinkaras.aimsense.service.SupabaseService;
import com.derinkaras.aimsense.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/userProfile")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final SupabaseService supabaseService;
    private final GunProfileService gunprofileService;
    private final GunProfileService gunProfileService;

    UserProfileController(UserProfileService userProfileService, SupabaseService supabaseService, GunProfileService gunProfileService) {
        this.userProfileService = userProfileService;
        this.supabaseService = supabaseService;
        this.gunProfileService = gunProfileService;
        this.gunprofileService = gunProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserProfileDto> createUserProfile(@RequestBody CreateProfileRequest userProfile) {
        UserProfileDto userProfileDto = userProfileService.create(userProfile);
        return ResponseEntity.ok(userProfileDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile() {
        UserProfileDto userProfileDto = userProfileService.getUserProfile();
        return ResponseEntity.ok(userProfileDto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UpdateProfileRequest req) {
        UserProfileDto userProfileDto = userProfileService.updateUserProfile(req);
        return ResponseEntity.ok(userProfileDto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUserProfile() {
        userProfileService.deleteUserProfile();
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount() {
        try {
            // 1. Delete user profile from your database
            userProfileService.deleteUserProfile();
            // 2. Delete rifle profiles
            gunprofileService.deleteAllGunProfiles();
            // 3. Delete from Supabase auth
            supabaseService.deleteUser();

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete account: " + e.getMessage()));
        }
    }


}