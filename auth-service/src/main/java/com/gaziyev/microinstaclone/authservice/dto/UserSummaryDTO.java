package com.gaziyev.microinstaclone.authservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryDTO {

    private String id;
    private String username;
    private String name;
    private String profilePicture;
}
