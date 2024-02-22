package com.gaziyev.microinstaclone.graphservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseDTO {

    private boolean success;
    private String message;
}
