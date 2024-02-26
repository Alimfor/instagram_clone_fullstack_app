package com.gaziyev.microinstaclone.authservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDTO {

    private Boolean success;
    private String message;
}
