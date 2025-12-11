package com.derinkaras.aimsense.dto.gunProfile;

import com.derinkaras.aimsense.model.UnitSystem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateGunProfileRequest {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Caliber is required")
    private String caliber;

    @NotNull(message = "Bullet weight is required")
    private BigDecimal bulletWeightGrains;

    @NotNull(message = "Ballistic coefficient is required")
    private BigDecimal ballisticCoefficient;

    @NotNull(message = "Muzzle velocity is required")
    private Integer muzzleVelocityFps;

    @NotNull(message = "Zero distance is required")
    private Integer zeroDistance;

    @NotNull(message = "Scope height is required")
    private BigDecimal scopeHeight;

    @NotNull(message = "Unit system is required")
    private UnitSystem unitSystem;

    // Optional
    private String gunPhotoUri;
}