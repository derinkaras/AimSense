package com.derinkaras.aimsense.controller;

import com.derinkaras.aimsense.dto.gunProfile.CreateGunProfileRequest;
import com.derinkaras.aimsense.dto.gunProfile.GunProfileDto;
import com.derinkaras.aimsense.dto.gunProfile.UpdateGunProfileRequest;
import com.derinkaras.aimsense.service.GunProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gunProfile")
public class GunProfileController {
    private final GunProfileService gunProfileService;


    GunProfileController(GunProfileService gunProfileService) {
        this.gunProfileService = gunProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<GunProfileDto> createGunProfile(@RequestBody @Valid CreateGunProfileRequest req){
        GunProfileDto gunProfileDto = gunProfileService.createGunProfile(req);
        return ResponseEntity.ok().body(gunProfileDto);

    }

    @GetMapping("/me")
    public ResponseEntity<List<GunProfileDto>> getGunProfiles(){
        List<GunProfileDto> listOfGunProfileDtos = gunProfileService.getAllGunProfiles();
        return  ResponseEntity.ok().body(listOfGunProfileDtos);
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<GunProfileDto> getSpecificGunProfile(@PathVariable String id){
        GunProfileDto specificGunProfile = gunProfileService.getSpecificGunProfile(id);
        return ResponseEntity.ok().body(specificGunProfile);
    }

    @PatchMapping("/me/{id}")
    public ResponseEntity<GunProfileDto> editSpecificGunProfile(@PathVariable String id,
                                                                @RequestBody UpdateGunProfileRequest req){
        return ResponseEntity.ok(gunProfileService.editSpecificGunProfile(id, req));
    }

    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> deleteSpecificGunProfile(@PathVariable String id){
        gunProfileService.deleteSpecificGunProfile(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/")
    public ResponseEntity<Void> deleteAllGunProfiles(){
        gunProfileService.deleteAllGunProfiles();
        return ResponseEntity.ok().build();
    }

}
