package com.ratection.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class User {

    private String username;
    private String password;
    private List<String> roles;

}
