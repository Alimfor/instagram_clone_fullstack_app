package com.gaziyev.microinstaclone.authservice.entity;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    private String displayName;
    private String profilePictureUrl;
    private Date birthday;
    private Set<Address> addresses;

}
