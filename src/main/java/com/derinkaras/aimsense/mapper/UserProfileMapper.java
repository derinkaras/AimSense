package com.derinkaras.aimsense.mapper;

import com.derinkaras.aimsense.dto.userProfile.UserProfileDto;
import com.derinkaras.aimsense.model.UserProfile;

public class UserProfileMapper {
    public static UserProfileDto userProfileToDto(UserProfile userProfile) {
        UserProfileDto userProfileDto = new UserProfileDto();

        if (userProfile.getFirstName() != null) {
            userProfileDto.setFirstName(userProfile.getFirstName());
        }
        if (userProfile.getLastName() != null) {
            userProfileDto.setLastName(userProfile.getLastName());
        }
        if (userProfile.getProfilePhotoUri() != null) {
            userProfileDto.setProfilePhotoUri(userProfile.getProfilePhotoUri());
        }

        return userProfileDto;

    }
}
