package com.starhealth.login.services;


import com.starhealth.login.payloads.LoginDto;
import com.starhealth.login.payloads.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}

