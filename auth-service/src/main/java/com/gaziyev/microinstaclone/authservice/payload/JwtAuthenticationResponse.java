package com.gaziyev.microinstaclone.authservice.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class JwtAuthenticationResponse {

	@NonNull
	private String accessToken;
	private String tokenType = "Bearer";
}
