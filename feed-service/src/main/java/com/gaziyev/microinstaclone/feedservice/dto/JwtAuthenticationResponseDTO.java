package com.gaziyev.microinstaclone.feedservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponseDTO {

    private Map<String, String> tokens;
    private String tokenType;
}
