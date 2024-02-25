package com.gaziyev.microinstaclone.authservice.entity;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public final static Role USER = new Role("USER");
    public final static Role SERVICE = new Role("SERVICE");

    private String name;
}
