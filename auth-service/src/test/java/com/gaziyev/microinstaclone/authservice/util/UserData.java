package com.gaziyev.microinstaclone.authservice.util;

import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class UserData {

    public static List<User> getUsers(User user, String otherUserName) {

        List<User> userList = new ArrayList<>(List.of(User.builder()
                .id("uuid-uuid-uuid-uuid-uuid")
                .username(otherUserName)
                .password("password")
                .email(otherUserName + "@gmail.com")
                .active(true)
                .userProfile(Profile.builder()
                        .displayName(otherUserName)
                        .profilePictureUrl("temp.jpg")
                        .birthday(new Date())
                        .build())
                .roles(new HashSet<>() {
                    {
                        add(Role.USER);
                    }
                })
                .build()));

        if (user != null) {
            userList.add(user);
        }

        return userList;


    }
}

