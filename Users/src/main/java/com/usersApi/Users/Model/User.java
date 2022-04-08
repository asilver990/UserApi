package com.usersApi.Users.Model;

import lombok.Data;

@Data
public class User {

    private String first_name;
    private String last_name;
    private String email;
    private String gender;
    private int document;
    private String documentType;
}
