package com.derinkaras.aimsense.dto.gunProfile;

import com.derinkaras.aimsense.model.UnitSystem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class GunProfileDto {
    private String id;
    private String name;
    private String caliber;
    private BigDecimal bulletWeightGrains;
    private BigDecimal ballisticCoefficient;
    private Integer muzzleVelocityFps;
    private Integer zeroDistance;
    private BigDecimal scopeHeight;
    private UnitSystem unitSystem;
    private String gunPhotoUri;

}
