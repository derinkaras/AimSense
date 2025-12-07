package com.derinkaras.aimsense.controller;

// package com.derinkaras.aimsense.note;

import com.derinkaras.aimsense.dto.CreateNoteRequest;
import com.derinkaras.aimsense.model.Note;
import com.derinkaras.aimsense.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository noteRepository;

    // POST /api/notes  → create note for current user
    @PostMapping
    public Note createNote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateNoteRequest request
    ) {
        String userId = jwt.getSubject();          // Supabase user id

        Note note = new Note();
        note.setOwnerId(userId);
        note.setTitle(request.title());
        note.setContent(request.content());

        return noteRepository.save(note);
    }

    // GET /api/notes  → list current user's notes
    @GetMapping
    public List<Note> myNotes(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return noteRepository.findByOwnerId(userId);
    }
}
