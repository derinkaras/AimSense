package com.derinkaras.aimsense.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

@Getter
@Setter

public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String userName;
}
