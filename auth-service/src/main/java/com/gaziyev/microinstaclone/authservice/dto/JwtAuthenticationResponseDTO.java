package com.gaziyev.microinstaclone.authservice.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class JwtAuthenticationResponseDTO {

    @NonNull
    private Map<String, String> tokens;
    private String tokenType = "Bearer";
}
