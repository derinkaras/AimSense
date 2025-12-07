package com.derinkaras.aimsense.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfileRequest {
    private String firstName;
    private String lastName;
    private String userName;
}
