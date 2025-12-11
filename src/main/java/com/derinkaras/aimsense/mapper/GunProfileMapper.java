package com.derinkaras.aimsense.mapper;

import com.derinkaras.aimsense.dto.gunProfile.GunProfileDto;
import com.derinkaras.aimsense.model.GunProfile;

public class GunProfileMapper {
    public static GunProfileDto gunProfileToDto(GunProfile gunProfile) {
        GunProfileDto dto = new GunProfileDto();
        dto.setId(gunProfile.getId());
        dto.setName(gunProfile.getName());
        dto.setCaliber(gunProfile.getCaliber());
        dto.setBulletWeightGrains(gunProfile.getBulletWeightGrains());
        dto.setBallisticCoefficient(gunProfile.getBallisticCoefficient());
        dto.setMuzzleVelocityFps(gunProfile.getMuzzleVelocityFps());
        dto.setZeroDistance(gunProfile.getZeroDistance());
        dto.setScopeHeight(gunProfile.getScopeHeight());
        dto.setUnitSystem(gunProfile.getUnitSystem());
        // Redundant
        if (gunProfile.getGunPhotoUri() != null) {
            dto.setGunPhotoUri(gunProfile.getGunPhotoUri());
        }
        return dto;
    }
}