package com.gaziyev.microinstaclone.authservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryDTO {

    private String id;
    private String username;
    private String name;
    private String profilePicture;
}
