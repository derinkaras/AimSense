package com.derinkaras.aimsense.repository;

import com.derinkaras.aimsense.model.GunProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GunProfileRepository extends JpaRepository<GunProfile, String> {

    Optional<GunProfile> findByIdAndSupabaseUserId(String id, String supabaseUserId);

    Optional<List<GunProfile>> findAllBySupabaseUserId(String supabaseUserId);

    void deleteAllBySupabaseUserId(String supabaseUserId);
}
