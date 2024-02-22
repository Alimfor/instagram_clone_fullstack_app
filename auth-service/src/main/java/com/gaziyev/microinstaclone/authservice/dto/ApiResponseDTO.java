package com.gaziyev.microinstaclone.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseDTO {

    private Boolean success;
    private String message;
}
