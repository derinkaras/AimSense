package com.derinkaras.aimsense.mapper;

import com.derinkaras.aimsense.dto.UserProfileDto;
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
        if (userProfile.getUserName() != null) {
            userProfileDto.setUserName(userProfile.getUserName());
        }
        return userProfileDto;

    }
}
