package com.derinkaras.aimsense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "gun_profiles")
public class GunProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "supabase_user_id", nullable = false)
    private String supabaseUserId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String caliber;

    @Column(name = "bullet_weight_grains", nullable = false, precision = 6, scale = 2)
    private BigDecimal bulletWeightGrains;

    @Column(name = "ballistic_coefficient", nullable = false, precision = 5, scale = 3)
    private BigDecimal ballisticCoefficient;

    @Column(name = "muzzle_velocity_fps", nullable = false)
    private Integer muzzleVelocityFps;

    @Column(name = "zero_distance", nullable = false)
    private Integer zeroDistance;

    @Column(name = "scope_height", nullable = false, precision = 3, scale = 2)
    private BigDecimal scopeHeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_system", nullable = false)
    private UnitSystem unitSystem;

    @Column(name = "gun_photo_uri")
    private String gunPhotoUri;
}