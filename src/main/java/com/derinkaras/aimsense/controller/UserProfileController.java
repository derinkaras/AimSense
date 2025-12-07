package com.derinkaras.aimsense.controller;

import com.derinkaras.aimsense.dto.CreateProfileRequest;
import com.derinkaras.aimsense.dto.UpdateProfileRequest;
import com.derinkaras.aimsense.dto.UserProfileDto;
import com.derinkaras.aimsense.model.UserProfile;
import com.derinkaras.aimsense.repository.UserProfileRepository;
import com.derinkaras.aimsense.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/userProfile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
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


}
