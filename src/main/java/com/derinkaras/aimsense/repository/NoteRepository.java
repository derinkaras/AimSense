package com.derinkaras.aimsense.repository;


import com.derinkaras.aimsense.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerId(String ownerId);
}
