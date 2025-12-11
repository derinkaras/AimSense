package com.derinkaras.aimsense.service;

import com.derinkaras.aimsense.dto.gunProfile.CreateGunProfileRequest;
import com.derinkaras.aimsense.dto.gunProfile.GunProfileDto;
import com.derinkaras.aimsense.exception.ResourceNotFoundException;
import com.derinkaras.aimsense.mapper.GunProfileMapper;
import com.derinkaras.aimsense.model.GunProfile;
import com.derinkaras.aimsense.repository.GunProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GunProfileService {
    private final GunProfileRepository repo;

    GunProfileService(GunProfileRepository repo){
        this.repo = repo;
    }

    public GunProfileDto createGunProfile(CreateGunProfileRequest req){
        GunProfile gunProfile = new GunProfile();
        gunProfile.setName(req.getName());
        gunProfile.setCaliber(req.getCaliber());
        gunProfile.setBallisticCoefficient(req.getBallisticCoefficient());
        gunProfile.setMuzzleVelocityFps(req.getMuzzleVelocityFps());
        gunProfile.setZeroDistance(req.getZeroDistance());
        gunProfile.setScopeHeight(req.getScopeHeight());
        gunProfile.setUnitSystem(req.getUnitSystem());
        // Redundant
        if (req.getGunPhotoUri() != null) {
            gunProfile.setGunPhotoUri(req.getGunPhotoUri());
        }

        // Get Supabase user id from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject(); // same as jwt.getClaimAsString("sub")
        gunProfile.setSupabaseUserId(supabaseUserId);

        return GunProfileMapper.gunProfileToDto(repo.save(gunProfile));
    }

    public GunProfileDto getSpecificGunProfile(String id) {

        // Get Supabase user id from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject(); // same as jwt.getClaimAsString("sub")
        GunProfile specificGunProfile = repo.findByIdAndSupabaseUserId(id, supabaseUserId)
                .orElseThrow(()-> new ResourceNotFoundException("Gun profile not found"));

        return GunProfileMapper.gunProfileToDto(specificGunProfile);

    }

    public List<GunProfileDto> getAllGunProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String supabaseUserId = jwt.getSubject();

        List<GunProfile> profiles = repo.findAllBySupabaseUserId(supabaseUserId)
                .orElse(new ArrayList<>());  // ✅ Return empty list if none found

        return profiles.stream()
                .map((profile)-> GunProfileMapper.gunProfileToDto(profile))
                .toList();
    }

}
