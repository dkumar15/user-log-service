package com.starhealth.login.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String name;
    private String username;
    private String role;
    private String email;
    private String password;
}

