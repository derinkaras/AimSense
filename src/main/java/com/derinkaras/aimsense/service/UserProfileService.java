package com.derinkaras.aimsense.service;

import com.derinkaras.aimsense.dto.CreateProfileRequest;
import com.derinkaras.aimsense.dto.UpdateProfileRequest;
import com.derinkaras.aimsense.dto.UserProfileDto;
import com.derinkaras.aimsense.exception.DuplicateResourceException;
import com.derinkaras.aimsense.exception.ResourceNotFoundException;
import com.derinkaras.aimsense.mapper.UserProfileMapper;
import com.derinkaras.aimsense.model.UserProfile;
import com.derinkaras.aimsense.repository.UserProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileDto create(CreateProfileRequest userProfile) {
        UserProfile userProfileEntity = new UserProfile();

        if (userProfile.getFirstName() != null) {
            userProfileEntity.setFirstName(userProfile.getFirstName());
        }
        if (userProfile.getLastName() != null) {
            userProfileEntity.setLastName(userProfile.getLastName());
        }
        if (userProfile.getUserName() != null) {
            if (userProfileRepository.existsByUserName(userProfile.getUserName())) {
                throw new DuplicateResourceException("Username already exists");
            }
            userProfileEntity.setUserName(userProfile.getUserName());
        }

        // Get Supabase user id from JWT (sub claim)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject(); // same as jwt.getClaimAsString("sub")

        userProfileEntity.setSupabaseUserId(supabaseUserId);

        return UserProfileMapper.userProfileToDto(userProfileRepository.save(userProfileEntity));
    }

    public UserProfileDto getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject();

        UserProfile userProfile = userProfileRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User Profile", supabaseUserId));

        return UserProfileMapper.userProfileToDto(userProfile);
    }

    public UserProfileDto updateUserProfile(UpdateProfileRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject();

        UserProfile userProfile = userProfileRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User Profile", supabaseUserId));

        if (req.getFirstName() != null) {
            userProfile.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null) {
            userProfile.setLastName(req.getLastName());
        }
        if (req.getUserName() != null) {
            if (userProfileRepository.existsByUserName(req.getUserName())) {
                throw new DuplicateResourceException("Username already exists");
            }
            userProfile.setUserName(req.getUserName());
        }

        return UserProfileMapper.userProfileToDto(userProfileRepository.save(userProfile));
    }

    public void deleteUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject();

        UserProfile userProfile = userProfileRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User Profile", supabaseUserId));

        userProfileRepository.delete(userProfile);
    }
}
