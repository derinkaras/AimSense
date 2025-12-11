package com.derinkaras.aimsense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Since my supabase user id is a UUID this should match style
    private String id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="profile_photo_uri")
    private String profilePhotoUri;

    @Column(name = "supabase_user_id", nullable = false, unique = true)
    private String supabaseUserId;

}
