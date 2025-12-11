package com.derinkaras.aimsense.repository;

import com.derinkaras.aimsense.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findBySupabaseUserId(String supabaseUserId);

    void deleteBySupabaseUserId(String supabaseUserId);
}
