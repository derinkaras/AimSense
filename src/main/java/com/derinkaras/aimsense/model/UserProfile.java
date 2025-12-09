package com.derinkaras.aimsense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Since my supabase user id is a UUID this should match style
    private String id;

    private String firstName;
    private String lastName;

    @Column(name="profile_photo_uri")
    private String profilePhotoUri;

    @Column(name = "supabase_user_id", nullable = false, unique = true)
    private String supabaseUserId;

}
