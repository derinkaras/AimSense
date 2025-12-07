package com.derinkaras.aimsense.dto;

public record CreateNoteRequest(
        String title,
        String content
) {}