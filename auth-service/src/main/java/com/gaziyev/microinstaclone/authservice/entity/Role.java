package com.gaziyev.microinstaclone.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public final static Role USER = new Role("USER");
    public final static Role SERVICE = new Role("SERVICE");

    private String name;
}
